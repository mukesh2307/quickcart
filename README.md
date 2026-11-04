# QuickCart — Blinkit-inspired Java web application

College-project demo built with Java 25 (LTS), JSP, Jakarta Servlets, JDBC, Maven, XAMPP MySQL/MariaDB, and Apache Tomcat 10.1.

## Included
- Product catalogue loaded from MySQL
- Search by product name/description and category filter
- Session-based shopping cart (add, update, remove, clear)
- Checkout with customer name and delivery address
- Saves orders and order items in a JDBC transaction
- Checks and decrements inventory on successful checkout

This is an educational local demo. It does not include customer accounts, real payments, delivery tracking, or production security features.

## Requirements
- JDK 25 (LTS; Tomcat 10.1 itself requires Java 11 or newer)
- Maven 3.9+
- Apache Tomcat 10.1.x
- XAMPP with MySQL/MariaDB
- VS Code with Extension Pack for Java (optional but helpful)

## 1. Import the database
1. Open XAMPP Control Panel and start **MySQL**. Apache in XAMPP is not required for the Java app.
2. Open `http://localhost/phpmyadmin`.
3. Use **Import** to import `database/blinkit_replica.sql`, or paste its contents into the SQL tab and run it.
4. Confirm the `blinkit_replica` database contains `categories` and `products`.

## 2. Check the database connection
Open `src/main/java/com/blinkit/util/DBConnection.java`.
Defaults are `localhost:3306`, database `blinkit_replica`, username `root`, empty password. If your XAMPP database uses a password or different port, change those values.

## 3. Open in VS Code
1. Extract this ZIP.
2. VS Code → **File → Open Folder** → choose `BlinkitReplica`.
3. Open Terminal → New Terminal and check:
   - `java -version`
   - `mvn -version`
4. If Maven is not installed, install Maven and add its `bin` folder to PATH. Restart VS Code after changing PATH.

## 4. Build the project
From the folder containing `pom.xml`, run:
```bash
mvn clean package
```
The WAR should appear at `target/blinkit-replica.war`.

## 5. Deploy to Tomcat 10.1
1. Download Tomcat 10.1 from the official Apache Tomcat website.
2. Extract it (ZIP distribution) to a folder such as `C:\apache-tomcat-10.1`.
3. Copy `target/blinkit-replica.war` to the Tomcat `webapps` folder.
4. Run `bin\startup.bat` from Command Prompt, or `bin\catalina.bat run` to keep logs visible.
5. Open `http://localhost:8080/blinkit-replica/`.
6. To stop Tomcat, run `bin\shutdown.bat`.

Tomcat 10.1 uses `jakarta.servlet.*`; the source and Maven dependency in this project are configured for it. Do not deploy this build to Tomcat 9 unchanged.

## 6. Test the app
1. Open the catalogue and check sample products appear.
2. Search `milk` or filter by a category.
3. Add products to cart, update quantities, and remove an item.
4. Continue to checkout, enter a name and address, then place the order.
5. In phpMyAdmin, check `orders` and `order_items`; check `products.stock` has decreased.

## Troubleshooting
- **404**: check the WAR filename and use `/blinkit-replica/`.
- **Database connection error**: start XAMPP MySQL and verify port, credentials, and SQL import.
- **Port 8080 already in use**: change Tomcat's HTTP Connector port in `conf/server.xml`. XAMPP Apache commonly uses port 80 and is a separate service.
- **`mvn` is not recognized**: Maven is missing from PATH; install/configure Maven and reopen terminal.
- **`jakarta.servlet` not found**: run Maven from the project root and verify the Tomcat 10.1 dependencies in `pom.xml`.
- **No products**: re-import the SQL and confirm `SELECT * FROM products;` returns rows.
