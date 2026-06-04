package education.scheme.welfare.service;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WelfareService {
    private JpaRepository jpaRepository;

    public ResponseEntity <Object> getScheme(String userId) {
        Map<String,String> map = new HashMap<>();
        map.put("userId", userId);
        map.put("status", "200");
        map.put("schemeId", "User Id successfully fetched!");

        return ResponseEntity.ok(map);
    }

}
