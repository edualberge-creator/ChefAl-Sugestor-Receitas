package util;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {

    private Properties properties;

    public ConfigLoader() {
        properties = new Properties();
        try (FileReader reader = new FileReader("config.properties")) {
            properties.load(reader);
        } catch (IOException e) {
            System.err.println("Erro: Não foi possível carregar o arquivo config.properties.");
            e.printStackTrace();
        }
    }

    /**
     * Busca a chave de API do arquivo de propriedades.
     * @return A chave da API como String.
     */
    public String getApiKey() {
        return properties.getProperty("API_KEY");
    }
}