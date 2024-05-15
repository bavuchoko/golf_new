package pjs.golf.common.sse_connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pjs.golf.app.game.dto.GameResponseDto;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.LogManager;

@Service
public class SseEmitterService {

    private final Map<Long, Map<Long, SseEmitter>> emitterMap = new ConcurrentHashMap<>();
    private static final Long TIMEOUT = 120L * 1000 * 60;
    private static final long RECONNECTION_TIMEOUT = 1000L;
    private final Logger log = LoggerFactory.getLogger(SseEmitterService.class);

    public SseEmitter subscribe(Long gameId, Long userId, String message) {
        SseEmitter emitter = getEmitter(gameId, userId);
        Map<Long, SseEmitter> userMap = new ConcurrentHashMap<>();
        userMap.put(userId, emitter);
        emitterMap.put(gameId, userMap);

        //초기 연결시에 응답 데이터를 전송할 수도 있다.
        try {
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                    .name("connect")
                    //event id (id: id-1) - 재연결시 클라이언트에서 `Last-Event-ID` 헤더에 마지막 event id 를 설정
                    .id( gameId + "_" + System.currentTimeMillis() )
                    .data(message)
                    .reconnectTime(RECONNECTION_TIMEOUT);
            emitter.send(event);
        } catch (IOException e) {
            log.error("failure send media position data, id={}, {}", gameId, e.getMessage());
        }
        return emitter;
    }

    public void broadcast(Long gameId, String message) {
        Map<Long, SseEmitter> userMap = emitterMap.get(gameId);
        userMap.forEach((id, player) -> {
            try {
                player.send(SseEmitter.event()
                        .name("broadcast")
                        .id( gameId + "_" + System.currentTimeMillis() )
                        .reconnectTime(RECONNECTION_TIMEOUT)
                        .data(message, MediaType.APPLICATION_JSON));
                log.info("sended notification, id={}, payload={}", id, message);
            } catch (IOException e) {
                //SSE 세션이 이미 해제된 경우
                log.error("fail to send emitter id={}, {}", id, e.getMessage());
            }
        });
    }

    private SseEmitter getEmitter(Long gameId, Long userId) {

        SseEmitter emitter = new SseEmitter(TIMEOUT);
        //연결 세션 timeout 이벤트 핸들러 등록
        emitter.onTimeout(() -> {
            log.info("server sent event timed out : id={}", userId);
            //onCompletion 핸들러 호출
            emitter.complete();
        });

        //에러 핸들러 등록
        emitter.onError(e -> {
            log.info("server sent event error occurred : id={}, message={}", userId, e.getMessage());
            //onCompletion 핸들러 호출
            emitter.complete();
        });

        //SSE complete 핸들러 등록
        emitter.onCompletion(() -> {
            if (emitterMap.get(gameId).remove(userId) != null) {
                log.info("server sent event removed in emitter cache: id={}", userId);
            }
            log.info("disconnected by completed server sent event: id={}", userId);
        });
        return emitter;
    }
}
