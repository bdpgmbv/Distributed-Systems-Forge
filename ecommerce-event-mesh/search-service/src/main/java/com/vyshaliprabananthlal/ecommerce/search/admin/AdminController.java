package com.vyshaliprabananthlal.ecommerce.search.admin;

/**
 * 3/1/26 - 21:36
 *
 * @author Vyshali Prabananth Lal
 */

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final EventReplayService replayService;

    public AdminController(EventReplayService replayService) {
        this.replayService = replayService;
    }

    @PostMapping("/replay-read-model")
    public ResponseEntity<String> triggerReplay() {
        replayService.replayFromBeginning();
        return ResponseEntity.ok("Replay initiated. Watch the logs as the Read Model rebuilds!");
    }
}
