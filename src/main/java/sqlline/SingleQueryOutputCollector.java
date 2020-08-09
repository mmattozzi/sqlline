package sqlline;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class SingleQueryOutputCollector {

    public abstract void addResultSet(ResultSet rs) throws SQLException;
    public abstract void close() throws IOException;

}
