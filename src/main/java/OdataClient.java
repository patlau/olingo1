import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetIteratorRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataServiceDocumentRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.*;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.format.ContentType;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class OdataClient {
    public static void main(String[] args) {
        ODataClient client = ODataClientFactory.getClient();

        String serviceRoot = "http://services.odata.org/V4/Northwind/Northwind.svc";
        ODataServiceDocumentRequest req =
                client.getRetrieveRequestFactory().getServiceDocumentRequest(serviceRoot);
        ODataRetrieveResponse<ClientServiceDocument> res = req.execute();

        ClientServiceDocument serviceDocument = res.getBody();

        Collection<String> entitySetNames = serviceDocument.getEntitySetNames();
        Map<String, URI> entitySets = serviceDocument.getEntitySets();
        Map<String,URI> singletons = serviceDocument.getSingletons();
        Map<String,URI> functionImports = serviceDocument.getFunctionImports();
        URI productsUri = serviceDocument.getEntitySetURI("Products");

        System.out.println(productsUri);

        ODataEntitySetIteratorRequest<ClientEntitySet, ClientEntity> request = client.getRetrieveRequestFactory().getEntitySetIteratorRequest(productsUri);
        request.setFormat(ContentType.APPLICATION_JSON);
        //request.setFormat(ODataFormat.ATOM);
        try {
            ODataRetrieveResponse<ClientEntitySetIterator<ClientEntitySet, ClientEntity>> response = request.execute();

            ClientEntitySetIterator<ClientEntitySet, ClientEntity> iterator = response.getBody();

            while (iterator.hasNext()) {
                ClientEntity product = iterator.next();
                List<ClientProperty> properties = product.getProperties();
                for (ClientProperty property : properties) {
                    String name = property.getName();
                    ClientValue value = property.getValue();
                    String valueType = value.getTypeName();
                    System.out.println(name + ": " + valueType);
                }
            }

        }  catch(ODataClientErrorException ex) {
            ex.printStackTrace();
        }
    }

}
