package com.nanoprojeto.delivery.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.nanoprojeto.delivery.daos.IDao;
import com.nanoprojeto.delivery.daos.OrderDao;
import com.nanoprojeto.delivery.daos.ProductDao;
import com.nanoprojeto.delivery.entities.Order;
import com.nanoprojeto.delivery.entities.Product;

public class OperationExecutor {
	
	private Connection conn;
	private ProductDao productDao;
	private IDao orderDao;

	public OperationExecutor(Connection conn) {
		super();
		this.conn = conn;
		this.productDao = new ProductDao(conn);
		this.orderDao = new OrderDao(conn);
	}
	
	private OrderDao getOrderDao() {
		return (OrderDao) this.orderDao;
	}
	
	public void execute(int option) throws SQLException {

        switch (option) {
            case 1 -> findAllProduct();
            case 2 -> findAllOrder();
            case 3 -> findByIdProduct();
            case 4 -> findByIdOrder();
            case 5 -> findOrderWithProducts();

            case 6 -> insertProduct();
            case 7 -> insertOrder();
            case 8 -> insertOrderWithProducts();

            case 9 -> updateProduct();
            case 10 -> updateOrder();

            case 11 -> deleteProduct();
            case 12 -> deleteOrder();
            case 13 -> deleteOrderProductRelationship();

            case 0 -> System.out.println("Encerrando aplicação.");
            default -> System.out.println("Opção inválida.");
        }
    }
	
	private void findAllProduct() throws SQLException {
		System.out.println("Executando findAll Product...");
		System.out.println();
		System.out.println();
		
		List<Product> products = productDao.findAll();
		products.forEach(System.out::println);
		
		System.out.println();
		System.out.println();
    }

    private void findAllOrder() throws SQLException {
        System.out.println("Executando findAll Order...");
        System.out.println();
		System.out.println();
		
    	List<Order> orders = getOrderDao().findAll();
    	orders.forEach(System.out::println);
		
		System.out.println();
		System.out.println();
    }

    private void findByIdProduct() throws SQLException {
        System.out.println("Executando findById Product...");
        System.out.println();
		System.out.println();
		
		Optional<Product> product = productDao.findById(3L);
		product.ifPresent(System.out::println);;
		
		System.out.println();
		System.out.println();
    }

    private void findByIdOrder() throws SQLException {
        System.out.println("Executando findById Order...");
        System.out.println();
   		System.out.println();
   		
   		Order order = getOrderDao().findById(2L);
       	System.out.println(order);
   		
   		System.out.println();
   		System.out.println();
       }

    private void findOrderWithProducts() throws SQLException {
        System.out.println("Executando Order + Products...");
        System.out.println();
		System.out.println();
		
		List<Order> ordersWithProducts = getOrderDao().findOrdersWithProducts();
    	ordersWithProducts.forEach(System.out::println);
		
		System.out.println();
		System.out.println();
    }

    private void insertProduct() throws SQLException {
        System.out.println("Executando insert Product...");
    }

    private void insertOrder() throws SQLException {
        System.out.println("Executando insert Order...");
    }

    private void insertOrderWithProducts() throws SQLException {
        System.out.println("Executando insert Order + Products...");
    }

    private void updateProduct() throws SQLException {
        System.out.println("Executando update Product...");
    }

    private void updateOrder() throws SQLException {
        System.out.println("Executando update Order...");
    }

    private void deleteProduct() throws SQLException {
        System.out.println("Executando delete Product...");
    }

    private void deleteOrder() throws SQLException {
        System.out.println("Executando delete Order...");
    }

    private void deleteOrderProductRelationship() throws SQLException {
        System.out.println("Executando delete relacionamento Order/Product...");
    }
}
