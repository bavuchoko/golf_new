package pjs.golf.common.sse_connection;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/sse", produces = "application/json;charset=UTF-8")
@RequiredArgsConstructor
public class SseEmitterController {

    private static final Logger log = LoggerFactory.getLogger(SseEmitterController.class);
    private final SseEmitterService sseEmitterService;

    @GetMapping("/disconnect/game/{gameId}")
    public void disconnect(
            @PathVariable("gameId") Long gameId,
            HttpServletRequest request
    ) {
        sseEmitterService.disconnect(gameId, request);
    }

    @GetMapping("/keep-alive")
    public String keepAlive(
    ) {
        log.info("connection keep-alive");
        return "ok";
    }

}
