package pjs.golf.common.sse_connection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.game.dto.GameResponseDto;
import pjs.golf.common.WebCommon;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.LogManager;

@Service
public class SseEmitterService {

    private final Map<Long, Map<String, SseEmitter>> emitterMap = new ConcurrentHashMap<>();
    private static final Long TIMEOUT = 1L * 1000 * 60 * 2;
    private static final long RECONNECTION_TIMEOUT = 1000L;
    private final Logger log = LoggerFactory.getLogger(SseEmitterService.class);

    //todo 유저 ip 로 SseEmitter를 관리하며 모바일에서 lte <-> wifi 등 ip가 변경될 때마다 문제가 발생 할 수 있으므로 적절한 대응 방안이 필요함. 
    // 현재 생각중인건 갱신토큰으로 유저를 특정하는 방법 
    public SseEmitter subscribe(Long gameId, EntityModel entityModel, HttpServletRequest request) {

         String userIp = WebCommon.getClientIp(request);

        SseEmitter emitter = getEmitter(userIp);
        emitterMap.computeIfAbsent(gameId, k -> new ConcurrentHashMap<>()).put(userIp, emitter);
        emitterMap.forEach((gId, uMap) -> {
            uMap.forEach((uId, eM) -> {
                log.error("gameId={}, userId ={}",gId, uId);
            });




        });

        //초기 연결시에 응답 데이터를 전송할 수도 있다.
        try {
            String message = getJsonString(entityModel);
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                    .name("connect")
                    //event id (id: id-1) - 재연결시 클라이언트에서 `Last-Event-ID` 헤더에 마지막 event id 를 설정
                    .id( gameId + "_" + System.currentTimeMillis() )
                    .data(message)
                    .reconnectTime(RECONNECTION_TIMEOUT);
            emitter.send(event);
        } catch (IOException e) {
            log.error("failure send media position data, id={}, message={}", gameId, e.getMessage());
        }

        return emitter;
    }

    public void broadcast(Long gameId, EntityModel entityModel) {
        Map<String, SseEmitter> userMap = emitterMap.get(gameId);
        if(userMap != null) {
            userMap.forEach((userId, emitter) -> {
                try {
                    String message = getJsonString(entityModel);
                    emitter.send(SseEmitter.event()
                            .name("broadCast")
                            .id(gameId + "_" + System.currentTimeMillis())
                            .reconnectTime(RECONNECTION_TIMEOUT)
                            .data(message, MediaType.APPLICATION_JSON));
                    log.info("userId = {}", userId);
                    log.info("emitter = {}", emitter);
                    log.info("message = {}", message);
                } catch (IOException e) {
                    //SSE 세션이 이미 해제된 경우
                    log.error("fail to send emitter id={}, {}", userId, e.getMessage());
                }
            });
        }else{
            log.error("nobody subscribing this game");
        }
    }

    private SseEmitter getEmitter(String userIp) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        //연결 세션 timeout 이벤트 핸들러 등록
        emitter.onTimeout(() -> {
            log.info("server sent event timed out : userIp={}", userIp);
            //onCompletion 핸들러 호출
            emitter.complete();
        });

        //에러 핸들러 등록
        emitter.onError(e -> {
            log.info("server sent event error occurred : userIp={}, message={}", userIp, e.getMessage());
            //onCompletion 핸들러 호출
            emitter.complete();
        });

        //SSE complete 핸들러 등록
        emitter.onCompletion(() -> {
            System.out.println("SSE connection completed");
            // 연결 완료 시의 처리 로직
        });

        return emitter;
    }


    private String getJsonString(EntityModel entityModel) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(entityModel);
    }

    public void disconnect(Long gameId,HttpServletRequest request) {
        String userIp = WebCommon.getClientIp(request);
        try {
            emitterMap.get(gameId).remove(userIp);
            log.info("request to disConnect gameId ={} , userIp ={}", gameId, userIp);
        }catch (Exception e){
            log.info("destroy connection failed gameId ={} , userIp ={}", gameId, userIp);
        }
    }


}
