package fitplan_pro_backend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/avaliacoes")
@CrossOrigin(origins = "*")
public class AvaliacaoController {

    private static final List<Map<String, Object>> avaliacoesDB =
            new ArrayList<>();

    private static final AtomicLong contadorId = new AtomicLong(0);

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {
        synchronized (avaliacoesDB) {
            List<Map<String, Object>> copia = new ArrayList<>();

            for (Map<String, Object> avaliacao : avaliacoesDB) {
                copia.add(new HashMap<>(avaliacao));
            }

            return ResponseEntity.ok(copia);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(
            @RequestBody Map<String, Object> dados) {

        Map<String, Object> response = new HashMap<>();

        if (dados == null) {
            response.put("error", "Dados não informados");
            return ResponseEntity.badRequest().body(response);
        }

        Object pesoObj = dados.get("peso");
        Object alturaObj = dados.get("altura");

        if (pesoObj == null || alturaObj == null) {
            response.put("error", "Peso e altura são obrigatórios");
            return ResponseEntity.badRequest().body(response);
        }

        double peso;
        double altura;

        try {
            peso = Double.parseDouble(pesoObj.toString());
            altura = Double.parseDouble(alturaObj.toString());
        } catch (NumberFormatException e) {
            response.put("error", "Peso e altura devem ser números válidos");
            return ResponseEntity.badRequest().body(response);
        }

        if (!Double.isFinite(peso) || !Double.isFinite(altura)
                || peso <= 0 || altura <= 0) {

            response.put("error", "Peso e altura devem ser maiores que zero");
            return ResponseEntity.badRequest().body(response);
        }

        if (peso > 500 || altura > 300) {
            response.put("error", "Confira os valores de peso e altura");
            return ResponseEntity.badRequest().body(response);
        }

        double alturaMetros = altura / 100.0;
        double imcCalculado = peso / (alturaMetros * alturaMetros);

        Map<String, Object> nova = new HashMap<>();

        nova.put("id", contadorId.incrementAndGet());
        nova.put("peso", peso);
        nova.put("altura", altura);
        nova.put("imc", String.format(
                java.util.Locale.US, "%.1f", imcCalculado
        ));
        nova.put("data", LocalDate.now().toString());

        synchronized (avaliacoesDB) {
            avaliacoesDB.add(0, nova);
        }

        response.put("message", "Avaliação criada com sucesso");
        response.put("avaliacao", new HashMap<>(nova));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
