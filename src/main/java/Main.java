import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.*;
import org.jooq.impl.DSL;
import generated.tables.Tickets;
import generated.tables.records.TicketsRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        String userName = "";
        String password = "";
        String url = "";

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("email", "vlasova_s.1965@postgrespro.ru");
        requestMap.put("phone", "+70375206559");

        try (Connection conn = DriverManager.getConnection(url, userName, password)) {
            DSLContext create = DSL.using(conn, SQLDialect.POSTGRES);

            Result<TicketsRecord> result = create
                    .selectFrom(Tickets.TICKETS)
                    .where(conditionsFromMap(requestMap))
                    .fetch();

            String contactData = result.get(0).getContactData().toString();

            Map<String, Object> map = objectMapper.readValue(contactData, new TypeReference<Map<String,Object>>(){});

            System.out.println(map.entrySet());
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Set<Condition> conditionsFromMap(Map<String, String> requestMap)  {
        Set<Condition> conditions = new HashSet<>();
        for(Map.Entry<String, String> e : requestMap.entrySet()) {
            conditions.add(Tickets.TICKETS.CONTACT_DATA.contains(JSONB.valueOf("\"" + e.getKey() + "\": \"" + e.getValue() + "\"")));
        }
        return conditions;
    }
}
