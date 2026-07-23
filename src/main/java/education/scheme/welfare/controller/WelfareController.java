package education.scheme.welfare.controller;

import education.scheme.welfare.models.ExceptionEntity;
import education.scheme.welfare.service.WelfareService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        if (userId == null || userId.isEmpty()) {
            throw new RuntimeException("user_id is required");
        } else {
            return welfareService.getScheme(userId);
        }
    }

}
