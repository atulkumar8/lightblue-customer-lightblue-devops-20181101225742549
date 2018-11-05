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

import org.cts.com.domain.Customer;
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

	private static Logger logger = LoggerFactory.getLogger(CustomerController.class);

	private Database cloudant;
	@Autowired
	private CloudantPropertiesBean cloudantProperties;

	@PostConstruct
	private void init() throws MalformedURLException {
		try {
			String cldUrl = cloudantProperties.getProtocol() + "://" + cloudantProperties.getHost() + ":"
					+ cloudantProperties.getPort();
			logger.info("Connecting to cloudant at: " + cldUrl);
			final CloudantClient cloudantClient = ClientBuilder.url(new URL(cldUrl))
					.username(cloudantProperties.getUsername()).password(cloudantProperties.getPassword()).build();
			cloudant = cloudantClient.database(cloudantProperties.getDatabase(), true);
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * @return customer by username
	 */
	@RequestMapping(value = "/customer/search", method = RequestMethod.GET)
	@ResponseBody
	ResponseEntity<?> searchCustomers(@RequestHeader Map<String, String> headers,
			@RequestParam(required = true) String username) {
		final List<Customer> customers = cloudant.findByIndex("{ \"selector\": {\"username\": \"" + username + "\" } }",
				Customer.class);
		return ResponseEntity.ok(customers);
	}

	/**
	 * @return all customer
	 */
	@RequestMapping(value = "/customer", method = RequestMethod.GET)
	@ResponseBody
	ResponseEntity<?> getCustomers(@RequestHeader Map<String, String> hdrs) {
		List<Customer> allCusts = cloudant.getAllDocsRequestBuilder().includeDocs(true).build().getResponse()
				.getDocsAs(Customer.class);
		return ResponseEntity.ok(allCusts);

	}

	/**
	 * @return customer by id
	 */
	@RequestMapping(value = "/customer/{id}", method = RequestMethod.GET)
	ResponseEntity<?> getById(@RequestHeader Map<String, String> headers, @PathVariable String id) {
		final Customer cust = cloudant.find(Customer.class, id);
		return ResponseEntity.ok(cust);
	}

	/**
	 * Add customer
	 * 
	 * @return transaction status
	 */
	@RequestMapping(value = "/customer", method = RequestMethod.POST, consumes = "application/json")
	ResponseEntity<?> create(@RequestHeader Map<String, String> headers, @RequestBody Customer payload) {
		try {
			if (payload.getCustomerId() != null && cloudant.contains(payload.getCustomerId())) {
				return ResponseEntity.badRequest().body("Id " + payload.getCustomerId() + " already exists");
			}
			final List<Customer> customers = cloudant.findByIndex(
					"{ \"selector\": {\"username\": \"" + payload.getUsername() + "\" } }", Customer.class);
			if (!customers.isEmpty()) {
				return ResponseEntity.badRequest()
						.body("Customer with name" + payload.getUsername() + " already exists");
			}
			final Response resp = cloudant.save(payload);
			if (resp.getError() == null) {
				final URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
						.buildAndExpand(resp.getId()).toUri();
				return ResponseEntity.created(location).build();
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp.getError());
			}
		} catch (Exception ex) {
			logger.error("Error creating customer: " + ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error creating customer: " + ex.toString());
		}
	}
	@RequestMapping(value = "/items", method = RequestMethod.GET)
	ResponseEntity<?> getInventory() {
		return ResponseEntity.ok("[{\"id\":1,\"name\":\"one\"},{\"id\":2,\"name\":\"two\"}]");
	}

	@RequestMapping(value = "/items/{id}", method = RequestMethod.GET)
	ResponseEntity<?> getById(@PathVariable long id) {
		return ResponseEntity.ok("{\"id\":1,\"name\":\"one\"}");
	}
}
