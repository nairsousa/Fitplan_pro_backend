package fitplan_pro_backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuariosController {

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {
        return ResponseEntity.ok(AuthController.getUsuariosDB());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletar(
            @PathVariable String id) {

        Map<String, String> response = new HashMap<>();

        synchronized (AuthController.getUsuariosDB()) {
            List<Map<String, Object>> lista =
                    AuthController.getUsuariosDB();

            boolean removido = lista.removeIf(usuario ->
                    id.equals(String.valueOf(usuario.get("id")))
            );

            if (!removido) {
                response.put("error", "Usuário não encontrado");

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(response);
            }
        }

        response.put("message", "Usuário removido com sucesso");

        return ResponseEntity.ok(response);
    }
}
