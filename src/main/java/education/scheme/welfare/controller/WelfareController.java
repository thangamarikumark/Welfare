package education.scheme.welfare.controller;

import education.scheme.welfare.service.WelfareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/scheme")
public class WelfareController {
    @Autowired
    private WelfareService welfareService;

    @PostMapping("/welfare")
    public ResponseEntity<Object> GetScheme(@RequestBody Map<String, String> request) {
        String userId = request.get("user_id");
        System.out.println("userId = " + userId);
        System.out.println("request = " + request);
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        return welfareService.getScheme(userId);

    }

}
