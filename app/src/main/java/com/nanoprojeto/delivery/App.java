package com.nanoprojeto.delivery;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import com.nanoprojeto.delivery.config.ConnectionFactory;
import com.nanoprojeto.delivery.config.EnvFile;
import com.nanoprojeto.delivery.controllers.OperationExecutor;
import com.nanoprojeto.delivery.views.Menu;

public class App {
    public static void main(String[] args) {
        try {
        	Connection conn = ConnectionFactory.getConnection(EnvFile.ENV);
        	
        	Scanner scanner = new Scanner(System.in);

            Menu menu = new Menu(scanner);
            OperationExecutor executor = new OperationExecutor(conn);
            int option;
            do {
                option = menu.show();
                menu.clearScreen();
                executor.execute(option);
                menu.waitForEnter(option);
            } while (option != 0);

            scanner.close();
            
        }
        catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco: " + e.getMessage());
        }
        finally {
        	ConnectionFactory.closeConnection();
        }
    }
    
}
