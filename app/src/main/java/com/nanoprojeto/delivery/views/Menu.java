package com.nanoprojeto.delivery.views;

import java.util.Scanner;

public class Menu {
	private Scanner scanner = null;
	
	public Menu(Scanner scanner) {
		this.scanner = scanner;
	}
	
	public int show() {
		clearScreen();
		
        System.out.println("""
                
                ==========================
                         CRUD JDBC
                ==========================
                
                CONSULTA
                1 - findAll Product
                2 - findAll Order
                3 - findById Product
                4 - findById Order
                5 - AllOrders + Products
                13 - Order + Products
                
                INSERÇÃO
                6 - Product
                7 - Order
                8 - Order + Products
                
                ATUALIZAÇÃO
                9  - Product
                10 - Order
                
                DELEÇÃO
                11 - Product
                12 - Order
                14 - Relacionamento Order/Product
                
                OUTROS CASOS
                15 - Pagination
                16 - Isolation And Concurrency by Price
                17 - Isolation And Concurrency by Version
                18 - Batch mode
                
                0 - Sair
                
                ==========================
                """);

        System.out.print("Escolha uma operação: ");

        return scanner.nextInt();
    }
	
	public void clearScreen() {
		System.out.print("\n".repeat(100));
	}
	
	public void waitForEnter(Integer option) {
	    System.out.println();
	    System.out.println("Pressione ENTER para " + (option.intValue() == 0 ? "sair... " : " voltar ao menu..."));
	    scanner.nextLine();
	    scanner.nextLine();
	}
}
