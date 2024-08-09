# Point of Sale Application
Welcome to the Point of Sale (POS) Application, a comprehensive solution developed using Java Spring, Hibernate, Maven, Ajax, jQuery, Thymeleaf, Jetty Server, MySQL, HTML, CSS, and JavaScript.

## Installation
Follow these step-by-step instructions to set up the POS Application on your local machine:

### Prerequisites
- Java Development Kit (JDK) installed
- Maven installed
- MySQL database server installed and running

#### Steps to install the pos app:

1. **Clone the repository**
    ```bash
   git clone https://github.com/Hackovator/pos-app
   cd pos-app

2. **Configure MySQL Database**

   - Create a database named **pos**
   
   - Edit the pos-app/posapp.properties file as per your MySQL configuration explained as follows:
     ```bash
     jdbc.username=[YOUR_MYSQL_USER_NAME]
     jdbc.password=[YOUR_MYSQL_USER_PASSWORD]
   - **Note:** Create a separate MySQL user and give it the privileges to access the pos database and do CRUD operations
   

4. **Build and Run**
    ```bash
    mvn clean install
    cd pos-app
    mvn jetty:run

5. **Access the Application:**
    Open your web browser and go to http://localhost:9000/

6. **Add an admin user in MySql Database by using the following command**
   ```bash
   Insert into users values(1, "admin@gmail.com", "admin", "supervisor");

7. **Login with the following email id and password**

   Email Id: admin@gmail.com

   Password: admin

   **Note:** These are the details that we have inserted into MySQL database in the previous command.


### Features

1. **Authentication**
      - Login and Signup:
      - Users can log in with their credentials.
      - New users can sign up to create an account.
2. **Brands**
    - Manage and categorize product brands.

3. **Products**
    - Store product details such as barcode, product name, brand, category, and MRP.

4. **Inventory**
    - Track the quantity of items for each product.

5. **Orders**
    - Store order details including date, time, product name, quantity, and selling price.
    - Ensure that the selling price is less than or equal to the MRP.

6. **Reports**

    - **Daily Sales Report:**
      - Displays the quantity of items sold on a daily basis.

    - **Overall Sales Report:**
      - Provides an overview of overall sales.

    - **Inventory Report:**
      - Shows the current inventory status.

    - **User Roles**
      - **Supervisor (Admin):**
        - Has full access to all sections.
        - Can perform CRUD operations.

      - **Operator:**
        - Can view UI elements for brands, products, etc.
        - Can place orders.
