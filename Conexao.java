package flowbank.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String HOST     = "aws-1-sa-east-1.pooler.supabase.com";
    private static final String PORT     = "6543";
    private static final String DATABASE = "postgres";
    private static final String USUARIO  = "postgres.bccmcftjyritnjlfasdr";
    private static final String SENHA    = "flowbankproje";

    private static final String URL =
            "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?sslmode=require";

    private Conexao() {}

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
