package helpers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import models.Package;
import play.libs.Json;

import java.util.List;

/**
 * Created by Mladen13 on 25.10.2015.
 */
public class JSONHelper {

    public static ObjectNode jsonUser(User u){

        ObjectNode user = Json.newObject();
        user.put("id", u.id);
        user.put("firstName", u.firstName);
        user.put("lastName", u.lastName);
        user.put("password", u.password);
        user.put("email", u.email);
        return user;

    }

    public static ObjectNode jsonPostOffice(PostOffice office){
        ObjectNode jsonPostOffice = Json.newObject();
        jsonPostOffice.put("id", office.id);
        jsonPostOffice.put("name", office.name);
        jsonPostOffice.put("address", office.address);
        return jsonPostOffice;
    }

    public static ObjectNode jsonPackage(models.Package pack){
        ObjectNode jsonPack = Json.newObject();
        jsonPack.put("id", pack.id);
        jsonPack.put("trackingNum", pack.trackingNum);
        jsonPack.put("recipientName", pack.recipientName);
        jsonPack.put("recipientAddress", pack.recipientAddress);
        jsonPack.put("weight", pack.weight);
        jsonPack.put("packageType", pack.packageType.toString());
        jsonPack.put("status", pack.shipmentPackages.get(0).status.toString());
        jsonPack.put("approved",pack.approved);
        return jsonPack;
    }

    public static ArrayNode jsonPostOfficeList(List<PostOffice> offices){
        ArrayNode jsonListPostOffices = new ArrayNode(JsonNodeFactory.instance);
        for(PostOffice office: offices){
            ObjectNode jsonOffice = jsonPostOffice(office);
            jsonListPostOffices.add(jsonOffice);
        }
        return jsonListPostOffices;
    }

    public static ArrayNode jsonPackagesList(List<Package> packages){
        ArrayNode jsonListPackages = new ArrayNode(JsonNodeFactory.instance);
        for(Package pack: packages){
            ObjectNode jsonPackage = jsonPackage(pack);
            jsonListPackages.add(jsonPackage);
        }
        return jsonListPackages;
    }

}
