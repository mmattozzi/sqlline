package sqlline;

import com.sun.rowset.CachedRowSetImpl;

import java.sql.SQLException;
import java.util.Hashtable;

public class EnhancedCacheResultSet extends CachedRowSetImpl {

    public EnhancedCacheResultSet() throws SQLException {
    }

    public EnhancedCacheResultSet(Hashtable hashtable) throws SQLException {
        super(hashtable);
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public boolean next() throws SQLException {
        try {
            return super.next();
        } catch (SQLException sqlException) {
            return false;
        }
    }
}
