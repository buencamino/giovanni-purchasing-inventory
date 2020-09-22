import java.sql.*;
import java.util.Vector;

public class dbconnect {
    private static final String database_driver = "com.mysql.jdbc.Driver";
    private static final String database_url = "jdbc:mysql://localhost:3306/giovannidb?useTimezone=true&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String user = "mike";
    private static final String password = "12345";

    private Connection conn = null;
    private Statement statement = null;
    private ResultSet rset = null;

    public void connect() throws Exception
    {
        try
        {
            Class.forName(database_driver);
            conn = DriverManager.getConnection(database_url, user, password);
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public ResultSet verifyCredentials (String username, String password) throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select * from tbl_user where username = '" + username + "' and password = '" + password + "'");
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public void addRequisitionslip (String project, String location, String fullname, String date, Vector<Vector<Object>> data, String purpose, String deliverytype, String time) throws Exception {
        String buffid = null;

        try
        {
            connect();
            PreparedStatement pstatement = conn.prepareStatement("insert into tbl_requisition (project, location, purpose, preparedby, date, deliverytype, time) values (?, ?, ?, ?, str_to_date('" + date + "', '%m/%d/%Y'), ?, ?)");
            pstatement.setString(1, project);
            pstatement.setString(2, location);
            pstatement.setString(3, purpose);
            pstatement.setString(4, fullname);
            pstatement.setString(5, deliverytype);
            pstatement.setString(6, time);

            pstatement.executeUpdate();

            statement = conn.createStatement();
            rset = statement.executeQuery("SELECT * FROM tbl_requisition ORDER BY requisition_num DESC LIMIT 1");

            while (rset.next())
            {
                buffid = String.valueOf(rset.getInt("requisition_num"));
            }

            for(int i = 0; i < data.size(); i++)
            {
                pstatement = conn.prepareStatement("insert into tbl_requisitionitems (requisition_num, quantity, units, description, enduser, category) values ('" + buffid + "', ?, ?, ?, ?, ?)");
                pstatement.setString(1, data.get(i).get(0).toString());
                pstatement.setString(2, data.get(i).get(1).toString());
                pstatement.setString(3, data.get(i).get(2).toString());
                pstatement.setString(4, data.get(i).get(3).toString());
                pstatement.setString(5, data.get(i).get(4).toString());

                pstatement.executeUpdate();

                pstatement = conn.prepareStatement("update tbl_stockcard set requisition_num = ? where description = '" + data.get(i).get(2).toString() + "' && permanent = 0");
                pstatement.setString(1, buffid);

                pstatement.executeUpdate();
            }
            statement.close();
            pstatement.close();
            conn.close();
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public ResultSet getPendingrequisitioncount () throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select count(*) from tbl_requisition where approvedby is null");
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public ResultSet getPendingreqlist () throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select * from tbl_requisition where approvedby is null");
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public ResultSet getReqinfo (String reqid) throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select *, date_format(date, '%m-%d-%Y') as newdate, TIME_FORMAT(time, '%h:%i %p') as timenow from tbl_requisition where requisition_num = " + reqid);
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public ResultSet getReqitems (String reqid) throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select * from tbl_requisitionitems where requisition_num = " + reqid);
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public void addProject(String projectname, String location, String ntpdate) throws Exception
    {
        try
        {
            connect();
            PreparedStatement pstatement = conn.prepareStatement("insert into tbl_project (project_name, location, ntpdate) values (?, ?, str_to_date('" + ntpdate + "', '%m/%d/%Y'))");
            pstatement.setString(1, projectname);
            pstatement.setString(2, location);

            pstatement.executeUpdate();

            pstatement.close();
            conn.close();
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public ResultSet getProjectlist () throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select * from tbl_project");
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public void deleteProject(String projectid) throws Exception
    {
        try
        {
            connect();
            statement = conn.createStatement();
            statement.executeUpdate("delete from tbl_project where project_id = " + projectid);
        }
        catch (Exception x)
        {
            throw x;
        }
    }

    public void addCanvasssheet(String reqnum, String supplier1, String supplier2, String supplier3, String terms1, String terms2, String terms3, Vector<Vector<Object>> data, String canvassedby, String recommendations, String time, String datenow) throws Exception {
        String buffid = null;

        try
        {
            connect();
            PreparedStatement pstatement = conn.prepareStatement("insert into tbl_canvass (requisition_num, supplier1, supplier2, supplier3, terms1, terms2, terms3, canvassedby, recommendations, time, date) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, str_to_date('" + datenow + "', '%m/%d/%Y'))");
            pstatement.setString(1, reqnum);
            pstatement.setString(2, supplier1);
            pstatement.setString(3, supplier2);
            pstatement.setString(4, supplier3);
            pstatement.setString(5, terms1);
            pstatement.setString(6, terms2);
            pstatement.setString(7, terms3);
            pstatement.setString(8, canvassedby);
            pstatement.setString(9, recommendations);
            pstatement.setString(10, time);

            pstatement.executeUpdate();

            for(int i = 0; i < data.size(); i++)
            {
                statement = conn.createStatement();
                rset = statement.executeQuery("SELECT * FROM tbl_canvass ORDER BY canvass_id DESC LIMIT 1");

                while (rset.next())
                {
                    buffid = String.valueOf(rset.getInt("canvass_id"));
                }

                pstatement = conn.prepareStatement("insert into tbl_canvassitems (canvass_id, item_id, quantity, description, unit1, amount1, unit2, amount2, unit3, amount3) values ('" + buffid + "', ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                pstatement.setString(1, data.get(i).get(0).toString());
                pstatement.setString(2, data.get(i).get(1).toString());
                pstatement.setString(3, data.get(i).get(2).toString());
                pstatement.setString(4, data.get(i).get(3).toString());
                pstatement.setString(5, data.get(i).get(4).toString());
                pstatement.setString(6, data.get(i).get(5).toString());
                pstatement.setString(7, data.get(i).get(6).toString());
                pstatement.setString(8, data.get(i).get(7).toString());
                pstatement.setString(9, data.get(i).get(8).toString());

                pstatement.executeUpdate();
            }
            statement.close();
            pstatement.close();
            conn.close();
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public ResultSet getCanvass () throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select * from tbl_canvass");
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public ResultSet getCanvassinfo (String canvassid) throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select * from tbl_canvass where canvass_id = " + canvassid);
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public ResultSet getCanvassitems (String canvassid) throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select * from tbl_canvassitems where canvass_id = " + canvassid);
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public void addPurchaseorder(String canvassnum, String preparedby, String supplier1, String supplier2, String supplier3, Vector<Vector<Object>> data, String date, int supplierchosen, String time) throws Exception {
        String buffid = null;
        String projectname = null, rsnum = null;

        try
        {
            connect();

            rset = null;
            statement = conn.createStatement();
            rset = statement.executeQuery("SELECT requisition_num FROM tbl_canvass where canvass_id = " + canvassnum);

            while (rset.next())
            {
                rsnum = String.valueOf(rset.getInt("requisition_num"));
            }

            rset = null;
            statement = conn.createStatement();
            rset = statement.executeQuery("SELECT project FROM tbl_requisition where requisition_num = " + rsnum);

            while (rset.next())
            {
                projectname = rset.getString("project");
            }

            PreparedStatement pstatement = conn.prepareStatement("insert into tbl_purchaseorder (canvass_id, preparedby, date, projectname, supplierchosen, time) values (?, ?, str_to_date('" + date + "', '%m/%d/%Y'), ?, " + supplierchosen + ", ?)");
            pstatement.setString(1, canvassnum);
            pstatement.setString(2, preparedby);
            pstatement.setString(3, projectname);
            pstatement.setString(4, time);

            pstatement.executeUpdate();

            rset = null;

            statement = conn.createStatement();
            rset = statement.executeQuery("SELECT * FROM tbl_purchaseorder ORDER BY purchaseorder_id DESC LIMIT 1");

            while (rset.next())
            {
                buffid = String.valueOf(rset.getInt("purchaseorder_id"));
            }

            rset = null;

            for(int i = 0; i < data.size(); i++)
            {
                String unit;

                unit = null;

                statement = conn.createStatement();
                rset = statement.executeQuery("SELECT * FROM tbl_requisitionitems where item_id = " + data.get(i).get(0).toString());

                while (rset.next())
                {
                    unit = rset.getString("units");
                }

                pstatement = conn.prepareStatement("insert into tbl_purchaseorderitems (purchaseorder_id, item_id, quantity, unit, description, unitprice, amount, supplierchosen, suppliername) values ('" + buffid + "', ?, ?, ?, ?, ?, ?, ?, ?)");
                //item id
                pstatement.setString(1, data.get(i).get(0).toString());
                //quantity
                pstatement.setString(2, data.get(i).get(1).toString());
                //unit
                pstatement.setString(3, unit);
                //description
                pstatement.setString(4, data.get(i).get(2).toString());
                //unitprice
                if (data.get(i).get(9).toString().equals("Supplier 1"))
                    pstatement.setString(5, data.get(i).get(3).toString());
                else if (data.get(i).get(9).toString().equals("Supplier 2"))
                    pstatement.setString(5, data.get(i).get(5).toString());
                else if (data.get(i).get(9).toString().equals("Supplier 3"))
                    pstatement.setString(5, data.get(i).get(7).toString());
                //amount
                if (data.get(i).get(9).toString().equals("Supplier 1"))
                    pstatement.setString(6, data.get(i).get(4).toString());
                else if (data.get(i).get(9).toString().equals("Supplier 2"))
                    pstatement.setString(6, data.get(i).get(6).toString());
                else if (data.get(i).get(9).toString().equals("Supplier 3"))
                    pstatement.setString(6, data.get(i).get(8).toString());
                //supplierchosen
                if (data.get(i).get(9).toString().equals("Supplier 1"))
                    pstatement.setString(7, "1");
                else if (data.get(i).get(9).toString().equals("Supplier 2"))
                    pstatement.setString(7, "2");
                else if (data.get(i).get(9).toString().equals("Supplier 3"))
                    pstatement.setString(7, "3");
                //suppliername
                if (data.get(i).get(9).toString().equals("Supplier 1"))
                    pstatement.setString(8, supplier1);
                else if (data.get(i).get(9).toString().equals("Supplier 2"))
                    pstatement.setString(8, supplier2);
                else if (data.get(i).get(9).toString().equals("Supplier 3"))
                    pstatement.setString(8, supplier3);

                pstatement.executeUpdate();
            }
            statement.close();
            pstatement.close();
            conn.close();
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public ResultSet getPurchaseorder () throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select *, TIME_FORMAT(time, '%h:%i %p') as timerecorded, date_format(date, '%m/%d/%Y') as newdate from tbl_purchaseorder");
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public ResultSet getPurchaseorderinfo (String purchaseid) throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select *, date_format(date, '%m-%d-%Y') as newdate, TIME_FORMAT(time, '%h:%i %p') as timerecorded from tbl_purchaseorder where purchaseorder_id = " + purchaseid);
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public ResultSet getPurchaseorderitems (String purchaseid) throws Exception{
        rset = null;
        ResultSet rset1 = null;
        int supplier = 0;

        try
        {
            connect();
            statement = conn.createStatement();
            rset1 = statement.executeQuery("select supplierchosen from tbl_purchaseorder where purchaseorder_id = " + purchaseid);

            while (rset1.next())
            {
                supplier = rset1.getInt("supplierchosen");
            }

            rset = statement.executeQuery("select * from tbl_purchaseorderitems where purchaseorder_id = " + purchaseid + " and  supplierchosen = '" + supplier + "'");
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public ResultSet getSupplierlist () throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select * from tbl_supplier");
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public void deleteSupplier(String supplierid) throws Exception
    {
        try
        {
            connect();
            statement = conn.createStatement();
            statement.executeUpdate("delete from tbl_supplier where supplier_id = " + supplierid);
        }
        catch (Exception x)
        {
            throw x;
        }
    }

    public void addSupplier(String suppliername, String accountname, String accountnum) throws Exception
    {
        try
        {
            connect();
            PreparedStatement pstatement = conn.prepareStatement("insert into tbl_supplier (suppliername, accountname, accountnum) values (?, ?, ?)");
            pstatement.setString(1, suppliername);
            pstatement.setString(2, accountname);
            pstatement.setString(3, accountnum);

            pstatement.executeUpdate();

            pstatement.close();
            conn.close();
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public ResultSet getStocklist () throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select description from tbl_stockcard");
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public ResultSet getStocksearch (String description) throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select description from tbl_stockcard where description like '%" + description + "%'");
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public boolean checkDuplicateitem (String description) throws Exception
    {
        rset = null;
        int i = 0;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select * from tbl_stockcard where description = '" + description + "'");
        }
        catch (Exception e)
        {
            throw e;
        }

        while (rset.next())
        {
            i++;
        }

        if (i > 0)
            return false;
        else if (i == 0)
            return true;
        return false;
    }

    public void addStock (String description) throws Exception
    {
        connect();
        PreparedStatement pstatement = conn.prepareStatement("insert into tbl_stockcard (description, permanent) values (?, 0)");
        pstatement.setString(1, description);

        pstatement.executeUpdate();
        pstatement.close();
    }

    public void setApprovepo(String poid) throws Exception
    {
        try
        {
            connect();
            statement = conn.createStatement();
            statement.executeUpdate("update tbl_purchaseorder set approved = 1 where purchaseorder_id = " + poid);
        }
        catch (Exception x)
        {
            throw x;
        }
    }

    public String checkVouchertype (String poid) throws Exception
    {
        int canvassid = 0;
        int supplierchosen = 0;
        String terms = null;
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select canvass_id, supplierchosen from tbl_purchaseorder where purchaseorder_id = " + poid);

            while (rset.next())
            {
                canvassid = rset.getInt("canvass_id");
                supplierchosen = rset.getInt("supplierchosen");
            }

            rset = null;

            statement = conn.createStatement();
            rset = statement.executeQuery("select terms" + supplierchosen + " from tbl_canvass where canvass_id = " + canvassid);

            while (rset.next())
            {
                terms = rset.getString("terms" + supplierchosen);
            }

            return terms;
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public ResultSet getSupplierinfo (String suppliername) throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select accountname, accountnum from tbl_supplier where suppliername = '" + suppliername + "'");
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public String getSuppliername (String supplierchosen, String canvassid) throws Exception{
        rset = null;
        String suppliername;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select supplier" + supplierchosen + " from tbl_canvass where canvass_id = " + canvassid);
            rset.first();
            suppliername = rset.getString("supplier" +supplierchosen);
        }
        catch (Exception e)
        {
            throw e;
        }

        return suppliername;
    }

    public void addVouchercheck (String poid, String date, String suppliername, String accountname, String accountnum, String time) throws Exception
    {
        connect();
        PreparedStatement pstatement = conn.prepareStatement("insert into tbl_vouchercheck (purchaseorder_id, suppliername, accountname, accountnum, date, time) values (?, ?, ?, ?, str_to_date('" + date + "', '%m/%d/%Y'), ?)");
        pstatement.setInt(1, Integer.parseInt(poid));
        pstatement.setString(2, suppliername);
        pstatement.setString(3, accountname);
        pstatement.setString(4, accountnum);
        pstatement.setString(5, time);

        pstatement.executeUpdate();
        pstatement.close();
    }

    public ResultSet checkDuplicatevouchercheck (String poid) throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select * from tbl_vouchercheck where purchaseorder_id = '" + poid + "'");
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public ResultSet getBanklist () throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select * from tbl_bank");
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public void addBank(String bankname, String accountname, String accountnum) throws Exception
    {
        try
        {
            connect();
            PreparedStatement pstatement = conn.prepareStatement("insert into tbl_bank (bankname, accountname, accountnum) values (?, ?, ?)");
            pstatement.setString(1, bankname);
            pstatement.setString(2, accountname);
            pstatement.setString(3, accountnum);

            pstatement.executeUpdate();

            pstatement.close();
            conn.close();
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public void updatePobank(String bankid, String poid) throws Exception
    {
        try
        {
            connect();
            statement = conn.createStatement();
            statement.executeUpdate("update tbl_purchaseorder set bankchosen = " + bankid + " where purchaseorder_id = " + poid);
        }
        catch (Exception x)
        {
            throw x;
        }
    }

    public ResultSet getBankinfo (String bankid) throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select * from tbl_bank where bank_id = " + bankid);
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public ResultSet getSum (String poid) throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select sum(amount) as sum from tbl_purchaseorderitems where purchaseorder_id = " + poid);
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public ResultSet checkDuplicatevouchercash (String poid) throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select * from tbl_vouchercash where purchaseorder_id = '" + poid + "'");
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public void addVouchercash (String poid, String date, String suppliername, String project, String preparedby, String time) throws Exception
    {
        connect();
        PreparedStatement pstatement = conn.prepareStatement("insert into tbl_vouchercash (purchaseorder_id, suppliername, project, preparedby, date, time) values (?, ?, ?, ?, str_to_date('" + date + "', '%m/%d/%Y'), ?)");
        pstatement.setInt(1, Integer.parseInt(poid));
        pstatement.setString(2, suppliername);
        pstatement.setString(3, project);
        pstatement.setString(4, preparedby);
        pstatement.setString(5, time);

        pstatement.executeUpdate();
        pstatement.close();
    }

    public ResultSet getProject (String poid) throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select * from tbl_purchaseorder where purchaseorder_id = " + poid);
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public ResultSet checkDuplicatesupplier (String suppliername, String accountname, String accountnum) throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select * from tbl_supplier where suppliername = '" + suppliername + "' && accountname = '" + accountname + "' && accountnum = '" + accountnum + "'");
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public ResultSet checkDuplicateproject (String projectname, String location, String ntpdate) throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select * from tbl_project where project_name = '" + projectname + "' && location = '" + location + "' && ntpdate = str_to_date('" + ntpdate + "', '%m/%d/%Y')");
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }
    public ResultSet checkDuplicatebank (String bankname, String accountname, String accountnum) throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select * from tbl_bank where bankname = '" + bankname + "' && accountname = '" + accountname + "' && accountnum = '" + accountnum + "'");
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public void close() throws Exception
    {
        try
        {
            conn.close();
            statement.close();
            rset = null;
        }
        catch (Exception e)
        {
            throw e;
        }
    }
}
