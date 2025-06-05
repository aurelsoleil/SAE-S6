package sae.semestre.six.bill.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.json.JSONObject;

@Service
public class AssuranceClient {
    private final RestTemplate restTemplate = new RestTemplate();

    public Double getMontantRembourse(String nomProduit, String nomAssurance) {
        String url = UriComponentsBuilder.fromHttpUrl("http://localhost:8888/remboursement/search/remboursementByNomProduitAndNomAssurance")
                .queryParam("nomProduit", nomProduit)
                .queryParam("nomAssurance", nomAssurance)
                .toUriString();

        String response = restTemplate.getForObject(url, String.class);
        // Le MS retourne un objet Page, on prend le premier élément
        JSONObject page = new JSONObject(response);
        if (page.has("_embedded")) {
            var remboursements = page.getJSONObject("_embedded").getJSONArray("remboursements");
            if (remboursements.length() > 0) {
                return remboursements.getJSONObject(0).getDouble("montantRembourse");
            }
        }
        return 0.0;
    }
}