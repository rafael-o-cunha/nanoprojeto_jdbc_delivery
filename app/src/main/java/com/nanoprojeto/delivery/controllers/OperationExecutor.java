package com.nanoprojeto.delivery.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import com.nanoprojeto.delivery.config.ConnectionFactory;
import com.nanoprojeto.delivery.config.EnvFile;

import com.nanoprojeto.delivery.daos.IDao;
import com.nanoprojeto.delivery.daos.OrderDao;
import com.nanoprojeto.delivery.daos.ProductDao;
import com.nanoprojeto.delivery.entities.Order;
import com.nanoprojeto.delivery.entities.OrderStatus;
import com.nanoprojeto.delivery.entities.Product;

public class OperationExecutor {
    private Scanner scanner;
	private ProductDao productDao;
	private IDao orderDao;

	public OperationExecutor(Connection conn, Scanner scanner) {
		super();
        this.scanner = scanner;
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
            case 5 -> findAllOrdersWithProducts();

            case 6 -> insertProduct();
            case 7 -> insertOrder();
            case 8 -> insertOrderWithProducts();

            case 9 -> updateProduct();
            case 10 -> updateOrder();

            case 11 -> deleteProduct();
            case 12 -> deleteOrder();
            case 13 -> findOrderWithProducts();
            case 14 -> deleteOrderProductRelationship();
            
            case 15 -> pagination();
            case 16 -> isolationAndConcurrencyByPrice();
            case 17 -> isolationAndConcurrencyByVersion();
            case 18 -> batchMode();

            case 0 -> System.out.println("Encerrando aplicação.");
            default -> System.out.println("Opção inválida.");
        }
    }
	
	private void pause() {
        scanner.nextLine();
    }
	
	private void findAllProduct() throws SQLException {
		System.out.println("Executando findAll Product...");
		System.out.println();
		
		List<Product> products = productDao.findAll();
		products.forEach(System.out::println);
		
		System.out.println();
    }

    private void findAllOrder() throws SQLException {
        System.out.println("Executando findAll Order...");
		System.out.println();
		
    	List<Order> orders = getOrderDao().findAll();
    	orders.forEach(System.out::println);
		
		System.out.println();
    }

    private void findByIdProduct() throws SQLException {
        System.out.println("Executando findById Product...");
		System.out.println();
		
		Optional<Product> product = productDao.findById(3L);
		product.ifPresent(System.out::println);
		
		System.out.println();
    }

    private void findByIdOrder() throws SQLException {
        System.out.println("Executando findById Order...");
   		System.out.println();
   		
   		Optional<Order> order = getOrderDao().findById(2L);
   		order.ifPresent(System.out::println);
   		
   		System.out.println();
       }

    private void findAllOrdersWithProducts() throws SQLException {
        System.out.println("Executando Order + Products...");
		System.out.println();
		
		List<Order> ordersWithProducts = getOrderDao().findOrdersWithProducts();
    	ordersWithProducts.forEach(System.out::println);
		
		System.out.println();
    }
    
    

    private void insertProduct() throws SQLException {
        System.out.println("Executando insert Product...");
        System.out.println();
        
        Product pBefore = new Product();
        pBefore.setName("Pizza Portuguesa");
        pBefore.setPrice(55.0);
        pBefore.setDescription("Pizza com presunto, queijo e ovos.");
        pBefore.setImageUri("pizza-portuguesa.jpg");
        
        Product pAfter = productDao.create(pBefore);
     
        System.out.println(pAfter);
        
        System.out.println();
    }

    private void insertOrder() throws SQLException {
        System.out.println("Executando insert Order...");
        System.out.println();
        
        Order oBefore = new Order();
        oBefore.setLatitude(-22.9068);
        oBefore.setLongitude(-43.1729);
        oBefore.setMoment(Instant.now());
        oBefore.setStatus(OrderStatus.PENDING);
        
        Order oAfter = this.getOrderDao().create(oBefore);
        System.out.println(oAfter);
        
        System.out.println();
    }

    private void insertOrderWithProducts() throws SQLException {
        System.out.println("Executando insert Order + Products...");
        System.out.println();
        
        Optional<Product> p1 = productDao.findById(1L);
        Optional<Product> p2 = productDao.findById(4L);

        if(p1.isEmpty() || p2.isEmpty()) {
        	System.out.println("Um ou mais produtos não existem.");
        	return;
        }
        Order oBefore = new Order();
        oBefore.setLatitude(-42.000);
        oBefore.setLongitude(-42.4242);
        oBefore.setMoment(Instant.now());
        oBefore.setStatus(OrderStatus.PENDING);
        
        oBefore.getProducts().add(p1.get());
        oBefore.getProducts().add(p2.get());
        
        Order oAfter = this.getOrderDao().createOrderWithProducts(oBefore);
        System.out.println(oAfter);
        
        System.out.println();
    }
    

    
    private void updateProduct() throws SQLException {
        System.out.println("Executando update Product...");
        System.out.println();
        
        Optional<Product> pBefore = productDao.findById(1L);
        if(pBefore.isEmpty()) {
        	System.out.println("Produto não encontrado.");
        	return;
        }
        
        System.out.println("pBefore: " + pBefore.get());
        
        /**
         * pegando mesma ref de memória para evitar necessidade de copy...
         */
        Product pAfter = pBefore.get();
        pAfter.setPrice(42.0);

        boolean atualizado = productDao.update(pAfter);
        System.out.printf("Produto atualizado: %b\n pAfter: %s \n", atualizado, pAfter);
        
        System.out.println();
    }

    private void updateOrder() throws SQLException {
        System.out.println("Executando update Order...");
        System.out.println();
        
        Optional<Order> oBefore = this.getOrderDao().findById(1L);
        
        if (oBefore.isEmpty()) {
            System.out.println("Pedido não encontrado.");
            return;
        }

        System.out.println("oBefore: " + oBefore);
        
        Order oAfter = oBefore.get();
        oAfter.setLatitude(-22.9068);
        oAfter.setLongitude(-43.1729);
        oAfter.setMoment(Instant.now());
        oAfter.setStatus(OrderStatus.DELIVERED);

        boolean atualizado = this.getOrderDao().update(oAfter);
        System.out.printf(
            "Pedido atualizado: %b\n oAfter: %s \n",
            atualizado,
            oAfter
        );

        System.out.println();
    }
    
    

    private void deleteProduct() throws SQLException {
    	System.out.println("Executando delete Product...");
        System.out.println();
        
        Optional<Product> p = productDao.findById(5L);
        
        if(p.isEmpty()) {
        	System.out.println("Produto não encontrado.");
        	return;
        }
        
        Product pBeforeDelete = p.get();
        
        System.out.println(pBeforeDelete);
        
        boolean deletado = productDao.delete(pBeforeDelete);
        System.out.printf(
        		"Produto deletado: %b\n",
        		deletado
		);
        
        System.out.println();
    }

    private void deleteOrder() throws SQLException {
        System.out.println("Executando delete Order...");
        System.out.println();
        
        Optional<Order> o = this.getOrderDao().findById(5L);
        
        if(o.isEmpty()) {
        	System.out.println("Order não encontrada.");
        	return;
        }
        
        Order OBeforeDelete = o.get();
        
        System.out.println(OBeforeDelete);
        
        boolean deletada = this.getOrderDao().delete(OBeforeDelete);
        System.out.printf(
        		"Order deletada: %b\n",
        		deletada
		);
        
        System.out.println();
    }
    
    private void findOrderWithProducts() throws SQLException {
        System.out.println("Executando Order + Products...");
		System.out.println();
		
		Optional<Order> o = this.getOrderDao().findByIdWithProducts(2L);
		if(o.isEmpty()) {
        	System.out.println("Order não encontrada.");
        }
		
		Order order = o.get();
		System.out.println(order);
		
		System.out.println();
    }

    private void deleteOrderProductRelationship() throws SQLException {
        System.out.println("Executando delete relacionamento Order/Product...");
        System.out.println();
        
        Optional<Order> o = this.getOrderDao().findByIdWithProducts(1L);
       
        pause();
        
        System.out.println(o.get());
        
        pause();
        
        if(o.isEmpty()) {
        	System.out.println("Order não encontrada.");
        }
        
        Order order = o.get();
        if(!order.getProducts().isEmpty()) {
        	Optional<Product> p = productDao.findById(order.getProducts().get(0).getId());
        	
        	if(p.isEmpty()) {
            	System.out.println("Product não encontrado. Order não possui este Product");
            	return;
            }
        	
        	Product product = p.get();
        	boolean deletado = this.getOrderDao().deleteOrderProductRelationship(order.getId(), product.getId());
        	System.out.printf(
            		"Product retirado da order: %b\n",
            		deletado
    		);
        }
        
        System.out.println();
    }


    
    private void pagination() throws SQLException {
        System.out.println("Executando Pagination...");
		System.out.println();
		
        int page = 2;
        int size = 3;
		List<Product> products = productDao.findAll(page, size);
		products.forEach(System.out::println);
		
		System.out.println();
	}

    private void isolationAndConcurrencyByPrice() throws SQLException {
        System.out.println("Executando Isolation and Concurrency...");
		System.out.println();

        /**
         * Duas conexões independentes para simular dois usuários independentes
         * acessando uma aplicação de locais distintos e em sessões distintas.
        */
        Connection connA = ConnectionFactory.createNewConnection(EnvFile.ENV);
        Connection connB = ConnectionFactory.createNewConnection(EnvFile.ENV);

        try {
            ProductDao productDaoA = new ProductDao(connA);
            ProductDao productDaoB = new ProductDao(connB);

            /**
             * Contador usado para garantir que ambas threads tenha feito
             * select.
             */
            CountDownLatch bothRead = new CountDownLatch(2);

            /**
             * usado quando A sinaliza que terminou update e commit.
             */
            CountDownLatch aCommited = new CountDownLatch(1);
            
            /**
             *  Usei thread aqui para simular múltiplos usuários.
             */
            Thread threadA = new Thread(() -> concurrentUpdateByPrice(productDaoA, 42.99, bothRead, aCommited, true));
            Thread threadB = new Thread(() -> concurrentUpdateByPrice(productDaoB, 50.0, bothRead, aCommited, false));
            
            threadA.start();
            threadB.start();
            
            threadA.join();
            threadB.join();
            
        }
        catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Thread Interrompida.");
        }
        finally {
            connA.close();
            connB.close();
        }
		
		System.out.println();
	}

    private void concurrentUpdateByPrice(ProductDao productDao, Double newPrice, CountDownLatch bothRead, CountDownLatch aCommited, boolean threadA) {
        try {
            String thread = threadA ? "ThreadA" : "ThreadB";

            Optional<Product> pBefore = productDao.findById(1L);
            if(pBefore.isEmpty()) {
                System.out.println("Produto não encontrado.");
                return;
            }
            
            System.out.println(thread + " pBefore: " + pBefore.get());
          
            /**
             * pegando mesma ref de memória para evitar necessidade de copy...
             */
            Double oldPrice = pBefore.get().getPrice();
            Product pAfter = pBefore.get();
            pAfter.setPrice(newPrice);

            /**
             * a thread terminou a leitura
             * depois espera A e B terminarem o select
             */
            bothRead.countDown();
            bothRead.await();

            /**
             * Espera A atualizar e fazer commit.
             */
            if(!threadA) {
                aCommited.await();
            }

            boolean atualizado = productDao.concurrentUpdateByPrice(pAfter, oldPrice);
            if(atualizado) {
                System.out.printf("%s - Produto atualizado %b \n", thread, atualizado);
            }
            else {
                System.out.printf("%s - Erro ao atualizar Produto %b \n", thread, atualizado);
            }

            Optional<Product> pValidation = productDao.findById(1L);
            if(pValidation.isEmpty()) {
                System.out.println("Produto não encontrado.");
                return;
            }
            
            System.out.println(thread + "ª pValidation: " + pValidation.get());

            /**
             * Libera B somente depois que A terminar sua operação transacional.
             */
            if(threadA) {
                aCommited.countDown();
            }

        }
        catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        catch (SQLException e) { //capturei aqui pois a interface do runnable não emite SQLException
            e.printStackTrace();
        }
    }

    private void isolationAndConcurrencyByVersion() throws SQLException {
        System.out.println("Executando Isolation and Concurrency...");
		System.out.println();

        /**
         * Duas conexões independentes para simular dois usuários independentes
         * acessando uma aplicação de locais distintos e em sessões distintas.
        */
        Connection connA = ConnectionFactory.createNewConnection(EnvFile.ENV);
        Connection connB = ConnectionFactory.createNewConnection(EnvFile.ENV);

        try {
            ProductDao productDaoA = new ProductDao(connA);
            ProductDao productDaoB = new ProductDao(connB);

            /**
             * Contador usado para garantir que ambas threads tenha feito
             * select.
             */
            CountDownLatch bothRead = new CountDownLatch(2);

            /**
             * usado quando A sinaliza que terminou update e commit.
             */
            CountDownLatch aCommited = new CountDownLatch(1);

            /**
             *  Usei thread aqui para simular múltiplos usuários.
             */
            Thread threadA = new Thread(() -> concurrentUpdateByVersion(productDaoA, 42.42, bothRead, aCommited, true));
            Thread threadB = new Thread(() -> concurrentUpdateByVersion(productDaoB, 50.42, bothRead, aCommited, false));
            
            threadA.start();
            threadB.start();
            
            threadA.join();
            threadB.join();
            
        }
        catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Thread Interrompida.");
        }
        finally {
            connA.close();
            connB.close();
        }
		
		System.out.println();
	}

    private void concurrentUpdateByVersion(ProductDao productDao, Double newPrice, CountDownLatch bothRead, CountDownLatch aCommited, boolean threadA) { 
        try {
            String thread = threadA ? "ThreadA" : "ThreadB";
           
            Optional<Product> pBefore = productDao.findById(1L);
            if(pBefore.isEmpty()) {
                System.out.println("produto não encontrado.");
                return;
            }

            System.out.println(thread + " pBefore " + pBefore.get());

            /**
             * pegando mesma ref de memória para evitar necessidade de copy...
             */
            Product pAfter = pBefore.get();
            pAfter.setPrice(newPrice);


            /**
             * a thread terminou a leitura
             * depois espera A e B terminarem o select
             */
            bothRead.countDown();
            bothRead.await();

            /**
             * Espera A atualizar e fazer commit.
             */
            if(!threadA) {
                aCommited.await();
            }

            boolean atualizado = productDao.concurrentUpdateByVersion(pAfter);
            if(atualizado) {
                System.out.printf("%s - Produto atualizado %b \n", thread, atualizado);
            }
            else {
                System.out.printf("%s - Erro ao atualizar Produto %b \n", thread, atualizado);
            }

            Optional<Product> pValidation = productDao.findById(1L);
            if(pValidation.isEmpty()) {
                System.out.println("Produto não encontrado.");
                return;
            }

            System.out.println(thread + "ª pValidation: " + pValidation.get());

            /**
             * Libera B somente depois que A terminar sua operação transacional.
             */
            if(threadA) {
                aCommited.countDown();
            }

        }
        catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        catch(SQLException e) { //capturei aqui pois a interface do runnable não emite SQLException
            e.printStackTrace();
        }
    }

    private void batchMode() throws SQLException {
        System.out.println("Executando Batch mode...");
		System.out.println();
		
		System.out.println();
	}
}
