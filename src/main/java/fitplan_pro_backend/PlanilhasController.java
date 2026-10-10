package fitplan_pro_backend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/planilhas")
@CrossOrigin(origins = "*")
public class PlanilhasController {

    private static final List<Map<String, Object>> planilhasDB =
            new ArrayList<>();

    private static final AtomicLong contadorId = new AtomicLong(2);

    static {
        Map<String, Object> p1 = new HashMap<>();
        p1.put("id", 1L);
        p1.put("titulo", "Treino A - Hipertrofia");
        p1.put("foco", "Peito e Tríceps");
        p1.put("exercicios", 4);

        Map<String, Object> p2 = new HashMap<>();
        p2.put("id", 2L);
        p2.put("titulo", "Treino B - Membros Inferiores");
        p2.put("foco", "Quadríceps e Glúteos");
        p2.put("exercicios", 5);

        planilhasDB.add(p1);
        planilhasDB.add(p2);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {
        synchronized (planilhasDB) {
            List<Map<String, Object>> copia = new ArrayList<>();

            for (Map<String, Object> planilha : planilhasDB) {
                copia.add(new HashMap<>(planilha));
            }

            return ResponseEntity.ok(copia);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(
            @RequestBody Map<String, String> dados) {

        Map<String, Object> response = new HashMap<>();

        if (dados == null) {
            response.put("error", "Dados não informados");
            return ResponseEntity.badRequest().body(response);
        }

        String titulo = dados.get("titulo");
        String foco = dados.get("foco");

        if (titulo == null || titulo.isBlank()
                || foco == null || foco.isBlank()) {

            response.put(
                    "error",
                    "O título e o foco são obrigatórios"
            );

            return ResponseEntity.badRequest().body(response);
        }

        Map<String, Object> nova = new HashMap<>();

        nova.put("id", contadorId.incrementAndGet());
        nova.put("titulo", titulo);
        nova.put("foco", foco);
        nova.put("exercicios", 0);

        synchronized (planilhasDB) {
            planilhasDB.add(nova);
        }

        response.put("message", "Planilha criada com sucesso");
        response.put("planilha", new HashMap<>(nova));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
