package karikuncheva.dominosapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import karikuncheva.dominosapp.model.Address;
import karikuncheva.dominosapp.model.User;
import karikuncheva.dominosapp.model.products.Dessert;
import karikuncheva.dominosapp.model.products.Drink;
import karikuncheva.dominosapp.model.products.Pizza;
import karikuncheva.dominosapp.model.products.Product;

/**
 * Created by Mariela Zviskova on 11.4.2017 г..
 */

public class DBManager extends SQLiteOpenHelper {
    private static DBManager ourInstance;
  //  private static Context context;
    private static HashMap<String, User> registeredUsers;
    public static ArrayList<Pizza> pizzas = new ArrayList<>();
    public static ArrayList<Dessert> desserts = new ArrayList<>();
    public static ArrayList<Drink> drinks = new ArrayList<>();

    private static final String SQL_CREATE_USERS = "CREATE TABLE users(\n" +
            "\n" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            " username text NOT NULL,\n" +
            " password text NOT NULL,\n" +
            " email text NOT NULL,\n" +
            " name text,\n" +
            " phone text\n" +
            ");";

    private static final String SQL_CREATE_ADDRESS = "CREATE TABLE addresses(\n" +
            "\n" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            " town text NOT NULL,\n" +
            " neighbourhood text NOT NULL,\n" +
            " street text NOT NULL,\n" +
            " number text NOT NULL,\n" +
            " block text NOT NULL,\n" +
            " postCode text NOT NULL,\n" +
            " apartment text NOT NULL,\n" +
            " floor text NOT NULL,\n" +
            " idUser INTEGER NOT NULL,\n" +
            " FOREIGN KEY(idUser) REFERENCES users(id)\n" +
            ");";

    private static final String SQL_CREATE_PRODUCTS = "CREATE TABLE products(\n" +
            "\n" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            " productType text NOT NULL,\n" +
            " name text NOT NULL,\n" +
            " price double NOT NULL,\n" +
            " quantity integer NOT NULL,\n" +
            " imageId integer NOT NULL,\n" +
            " discPrice double,\n" +
            " type text,\n" +
            " size text,\n" +
            " ingredients text\n" +
            ");";

    public static DBManager getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new DBManager(context);
          //  DBManager.context = context;
            registeredUsers = new HashMap<>();
            loadUsers();
            loadProducts();
        }
        return ourInstance;
    }

    private DBManager(Context context) {
        super(context, "mydb", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USERS);
        db.execSQL(SQL_CREATE_PRODUCTS);
        db.execSQL(SQL_CREATE_ADDRESS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE users;");
        db.execSQL("DROP TABLE addresses");
        db.execSQL("DROP TABLE products");
        onCreate(db);
    }


    public boolean addUser(User u) {
        if (existsUser(u.getUsername())) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", u.getUsername());
        contentValues.put("password", u.getPassword());
        contentValues.put("email", u.getEmail());
        contentValues.put("name", u.getName());
        contentValues.put("phone", u.getPhoneNumber());
        long id = getWritableDatabase().insert("users", null, contentValues);
        u.setId((int) id);
        registeredUsers.put(u.getUsername(), u);
        return true;
    }

    public void addAddress(Address a) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("town", a.getTown());
        contentValues.put("neighbourhood", a.getNeighbourhood());
        contentValues.put("street", a.getStreet());
        contentValues.put("number", a.getNumber());
        contentValues.put("postCode", a.getPostCode());
        contentValues.put("block", a.getBlock());
        contentValues.put("apartment", a.getApartment());
        contentValues.put("floor", a.getFloor());
        contentValues.put("idUser", LoginActivity.loggedUser.getId());
        long id = getWritableDatabase().insert("addresses", null, contentValues);
        a.setId((int) id);
        a.setIdUser(LoginActivity.loggedUser.getId());
        //addresses.add(a);
        LoginActivity.loggedUser.getAddresses().add(a);
    }

    public void updateUser(String username) {

        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                String username = strings[0];

                ContentValues values = new ContentValues();
                values.put("username", LoginActivity.loggedUser.getUsername());
                values.put("password", LoginActivity.loggedUser.getPassword());
                values.put("email", LoginActivity.loggedUser.getEmail());
                values.put("name", LoginActivity.loggedUser.getName());
                values.put("phone", LoginActivity.loggedUser.getPhoneNumber());
                registeredUsers.remove(username);
                registeredUsers.put(username, LoginActivity.loggedUser);
                getWritableDatabase().update("users", values, "username = ?", new String[]{username});
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
            }
        }.execute(username);
    }

    private static void loadUsers() {

        Cursor cursor = ourInstance.getWritableDatabase().rawQuery("SELECT id, username, password, email, name, phone FROM users;", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String username = cursor.getString(cursor.getColumnIndex("username"));
            String password = cursor.getString(cursor.getColumnIndex("password"));
            String email = cursor.getString(cursor.getColumnIndex("email"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            User u = new User(username, password, email);
            u.setName(name);
            u.setPhoneNumber(phone);
            u.setId(id);
            u.setAddresses(loadAddresses(u.getId()));
            registeredUsers.put(username, u);

        }
    }

    public static ArrayList<Address> loadAddresses(int userId) {
        ArrayList<Address> addresses = new ArrayList<Address>();
        Cursor cursor = ourInstance.getWritableDatabase().rawQuery("SELECT id, town, neighbourhood, street, number, block, postCode, apartment, floor, idUser FROM addresses WHERE idUser = '" + userId + "';", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String town = cursor.getString(cursor.getColumnIndex("town"));
            String neighbourhood = cursor.getString(cursor.getColumnIndex("neighbourhood"));
            String street = cursor.getString(cursor.getColumnIndex("street"));
            String number = cursor.getString(cursor.getColumnIndex("number"));
            String block = cursor.getString(cursor.getColumnIndex("block"));
            String postCode = cursor.getString(cursor.getColumnIndex("postCode"));
            String apartment = cursor.getString(cursor.getColumnIndex("apartment"));
            String floor = cursor.getString(cursor.getColumnIndex("floor"));
            int idUser = cursor.getInt(cursor.getColumnIndex("idUser"));

            Address a = new Address(town, neighbourhood, street, number, block, postCode, apartment, floor);
            a.setId(id);
            a.setIdUser(idUser);
            addresses.add(a);
        }
        return addresses;
    }

    public static void addProducts() {

        pizzas.add(new Pizza("Margarita", 12.00, R.drawable.margarita, "Traditional", "Large", "Tomato sauce, mozzarella"));
        pizzas.add(new Pizza("Beast", 16.50, R.drawable.beast, "Traditional", "Large", "Tomato sauce, mozzarella, ham, bacon, spicy beef"));
        pizzas.add(new Pizza("Mediterraneo", 14.50, R.drawable.mediterraneo, "Traditional", "Large", "Tomato sauce, mozzarella, green peppers, feta cheese, olives"));
        pizzas.add(new Pizza("Carbonara", 14.50, R.drawable.carbonara, "Traditional", "Large", "Cream sauce, mozzarella, bacon, mushrooms"));
        pizzas.add(new Pizza("Alfredo", 15.50, R.drawable.alfredo, "Traditional", "Large", "Cream sauce, mozzarella, baby spinach, chicken"));
        pizzas.add(new Pizza("Vita", 14.50, R.drawable.vita, "Traditional", "Large", "Tomato sauce, mozzarella, baby spinach, feta cheese, tomatos"));
        pizzas.add(new Pizza("Chickenita", 18.50, R.drawable.chickenita, "Traditional", "Large", "BBQ sauce, mozzarella, chicken, pepperoni, tomatos, emmental"));
        pizzas.add(new Pizza("American Hot", 15.50, R.drawable.americanhot, "Traditional", "Large", "Tomato sauce, mozzarella, pepperoni, spicy peppers, onion"));
        pizzas.add(new Pizza("New York", 16.50, R.drawable.newyork, "Traditional", "Large", "Tomato sauce, mozzarella, bacon, cheddar, mushrooms"));
        pizzas.add(new Pizza("Bulgarian", 15.50, R.drawable.bulgaria, "Traditional", "Large", "Tomato sauce, mozzarella, onion, olives, green peppers, feta cheese"));

        desserts.add(new Dessert("Choko Pie", 6.50, R.drawable.chocopie, "Freshly oven baked puff pastry filled with Nutella"));
        desserts.add(new Dessert("Souflle", 6.50, R.drawable.souffle, "Chocolate lava cake filled with melted warm chocolate"));
        desserts.add(new Dessert("Nirvana", 2.90, R.drawable.nirvana, "Nirvana Pralines & Cream"));
        desserts.add(new Dessert("Mini Pancakes", 3.50, R.drawable.minipancakes, "12 puffy mini pancakes with banana tast"));

        drinks.add(new Drink("Coca-Cola", 2.80, R.drawable.cola, "1,25l"));
        drinks.add(new Drink("Fanta", 2.80, R.drawable.fanta, "1,25l"));
        drinks.add(new Drink("Sprite", 2.80, R.drawable.sprite, "1,25l"));
        drinks.add(new Drink("Nestea", 2.00, R.drawable.nestea, "1,25l"));
    }

    private static void loadProducts() {
        DBManager.addProducts();
        Cursor cursor = ourInstance.getWritableDatabase().rawQuery("SELECT id, productType, name, price, quantity, imageId, discPrice, type, " +
                "size, ingredients FROM products;", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String productType = cursor.getString(cursor.getColumnIndex("productType"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            double price = cursor.getDouble(cursor.getColumnIndex("price"));
            int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
            int imageId = cursor.getInt(cursor.getColumnIndex("imageId"));
            double discPrice = cursor.getDouble(cursor.getColumnIndex("descPrice"));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            String size = cursor.getString(cursor.getColumnIndex("size"));
            String ingredients = cursor.getString(cursor.getColumnIndex("ingredients"));
            Product p = new Pizza(name, price, imageId, type, size, ingredients);
            p.setName(name);
            p.setPrice(price);
            p.setpType(productType);
            p.setQuantity(quantity);
            p.setDiscPrice(discPrice);
            p.setId(id);

        }
    }

    public void deleteAddress(Address a) {
        getWritableDatabase().delete("addresses", "id = ?", new String[]{Integer.toString(a.getId())});

        //LoginActivity.loggedUser.getAddresses().remove(a);
    }

    public boolean existsUser(String username) {

        return registeredUsers.containsKey(username);
    }

    public User getUser(String username) {
        return registeredUsers.get(username);
    }
}