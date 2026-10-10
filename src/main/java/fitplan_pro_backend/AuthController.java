package fitplan_pro_backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final List<Map<String, Object>> usuariosDB =
            new ArrayList<>();

    @PostMapping("/cadastro")
    public ResponseEntity<Map<String, Object>> cadastro(
            @RequestBody Map<String, String> dados) {

        Map<String, Object> response = new HashMap<>();

        if (dados == null) {
            response.put("error", "Dados não informados");
            return ResponseEntity.badRequest().body(response);
        }

        String nome = dados.get("nome");
        String email = dados.get("email");
        String senha = dados.get("senha");

        if (nome == null || nome.isBlank()
                || email == null || email.isBlank()
                || senha == null || senha.isBlank()) {

            response.put(
                    "error",
                    "Preencha todos os campos obrigatórios"
            );

            return ResponseEntity.badRequest().body(response);
        }

        synchronized (usuariosDB) {

            for (Map<String, Object> usuario : usuariosDB) {
                if (email.equalsIgnoreCase(
                        (String) usuario.get("email"))) {

                    response.put(
                            "error",
                            "Este e-mail já está cadastrado"
                    );

                    return ResponseEntity
                            .status(HttpStatus.CONFLICT)
                            .body(response);
                }
            }

            Map<String, Object> novoUsuario = new HashMap<>();

            novoUsuario.put("id", UUID.randomUUID().toString());
            novoUsuario.put("nome", nome);
            novoUsuario.put("email", email);
            novoUsuario.put("telefone", dados.get("telefone"));
            novoUsuario.put("funcao", "Aluno");
            novoUsuario.put("status", "Ativo");
            novoUsuario.put("senha", senha);

            usuariosDB.add(novoUsuario);

            Map<String, Object> usuarioPublico =
                    new HashMap<>(novoUsuario);

            usuarioPublico.remove("senha");

            response.put(
                    "message",
                    "Aluno cadastrado com sucesso!"
            );

            response.put("usuario", usuarioPublico);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> dados) {

        Map<String, Object> response = new HashMap<>();

        if (dados == null) {
            response.put("error", "Informe e-mail e senha");
            return ResponseEntity.badRequest().body(response);
        }

        String email = dados.get("email");
        String senha = dados.get("senha");

        if (email == null || email.isBlank()
                || senha == null || senha.isBlank()) {

            response.put("error", "Informe e-mail e senha");

            return ResponseEntity.badRequest().body(response);
        }

        synchronized (usuariosDB) {

            for (Map<String, Object> usuario : usuariosDB) {

                String emailCadastrado =
                        (String) usuario.get("email");

                String senhaCadastrada =
                        (String) usuario.get("senha");

                if (email.equalsIgnoreCase(emailCadastrado)
                        && senha.equals(senhaCadastrada)) {

                    Map<String, Object> usuarioPublico =
                            new HashMap<>(usuario);

                    usuarioPublico.remove("senha");

                    response.put(
                            "message",
                            "Login realizado com sucesso"
                    );

                    response.put(
                            "token",
                            "fake-jwt-token-springboot"
                    );

                    response.put("usuario", usuarioPublico);

                    return ResponseEntity.ok(response);
                }
            }
        }

        response.put("error", "E-mail ou senha inválidos");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    public static List<Map<String, Object>> getUsuariosDB() {
        synchronized (usuariosDB) {
            return new ArrayList<>(usuariosDB);
        }
    }
}
