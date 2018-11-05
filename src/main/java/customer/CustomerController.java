package customer;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Calendar;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.org.lightcouch.NoDocumentException;

/**
 * REST Controller to manage Customer database
 *
 */
@RestController
public class CustomerController {
    
    private static Logger logger =  LoggerFactory.getLogger(CustomerController.class);
    
    private Database cloudant;
@Autowired
private CloudantPropertiesBean cloudantProperties;
@PostConstruct
private void init() throws MalformedURLException {
try {
String cldUrl = cloudantProperties.getProtocol() + "://" +cloudantProperties.getHost() + ":" +cloudantProperties.getPort();
logger.info("Connecting to cloudant at: " +cldUrl);
final CloudantClient cloudantClient = ClientBuilder.url(new URL(cldUrl)).username(cloudantProperties.getUsername()).password(cloudantProperties.getPassword()).build();
cloudant =cloudantClient.database(cloudantProperties.getDatabase(), true);
} catch (MalformedURLException e) {
logger.error(e.getMessage(), e);
throw e;
}
}
    
    /**
     * @return customer by username
     */
    @RequestMapping(value = "/customer/search", method = RequestMethod.GET)
    @ResponseBody ResponseEntity<?> searchCustomers(@RequestHeader Map<String, String> headers, @RequestParam(required=true) String username) {
        return  ResponseEntity.ok("[{\"user\":\"foo\"}]");
    }

    

    /**
     * @return all customer
     */
    @RequestMapping(value = "/customer", method = RequestMethod.GET)
    @ResponseBody ResponseEntity<?> getCustomers(@RequestHeader Map<String, String> hdrs) {
        return ResponseEntity.ok("[{\"user\":\"foo\"}]");
        
    }

    /**
     * @return customer by id
     */
    @RequestMapping(value = "/customer/{id}", method = RequestMethod.GET)
    ResponseEntity<?> getById(@RequestHeader Map<String, String> headers, @PathVariable String id) {
        return ResponseEntity.ok("{\"user\":\"foo\"}");
    }

    /**
     * Add customer 
     * @return transaction status
     */
    @RequestMapping(value = "/customer", method = RequestMethod.POST, consumes = "application/json")
    ResponseEntity<?> create(@RequestHeader Map<String, String> headers) {
        return ResponseEntity.ok("{\"user\":\"foo\"}");
    }


}
