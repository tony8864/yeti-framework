package io.github.tony8864;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<String, String> store = new ConcurrentHashMap<>();

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> getUser(@PathVariable("id") String id) {
        var user = store.get(id);
        return user != null
                ? ResponseEntity.ok(Map.of("id", id, "name", user))
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createUser(@RequestBody Map<String, String> body) {
        String id = UUID.randomUUID().toString();
        String name = body.get("name");
        store.put(id, name);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", id, "name", name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateUser(
            @PathVariable("id") String id,
            @RequestBody Map<String, String> body
    ) {
        if (!store.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        String name = body.get("name");
        store.put(id, name);
        return ResponseEntity.ok(Map.of("id", id, "name", name));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String id) {
        if (!store.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        store.remove(id);
        return ResponseEntity.noContent().build(); // 204
    }
}