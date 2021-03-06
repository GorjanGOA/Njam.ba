package models;
 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import models.orders.Cart;
import models.orders.CartItem;

import com.fasterxml.jackson.databind.JsonNode;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import play.Logger;
import play.db.ebean.Model;
import play.libs.Json;
 
@Entity
public class TransactionU extends Model {
 
	@Id
	public int id;
 
	@ManyToOne
	public Restaurant restaurant;
	
	public String contextToPay;
 
	public String paymentToPay;
 
	public String paymentExecutionToPay;
 
	public int userToPayId;
 
	public int cartToPayId;
	
	public String email;
	
	public double price;
	
	public String token;
	
	@OneToMany(mappedBy="transaction",cascade=CascadeType.ALL)
	public List<MetaItem> items = new ArrayList<MetaItem>();
	
	public Boolean approved = false;
	
	public Boolean refused = false;
	
	public long deliveryTime;
	public long orderTimeGap;
	public long lateTime;
	
	public Boolean refund;
	
	public static Finder<Long, TransactionU> find = new Finder<Long, TransactionU>(Long.class,
			TransactionU.class);
	
	
	public TransactionU(String contextToPay, String paymentToPay,
			String paymentExecutionToPay, int userToPayId,
			int cartToPayId, Restaurant restaurantToPay, String token, int deliveryTime) {
		
		email = User.find(userToPayId).email;
		price = Cart.find(cartToPayId).total;
		this.restaurant = restaurantToPay;
		this.contextToPay = contextToPay;
		this.paymentToPay = paymentToPay;
		this.paymentExecutionToPay = paymentExecutionToPay;
		this.userToPayId = userToPayId;
		this.cartToPayId = cartToPayId;
		this.items = new ArrayList<MetaItem>(0);
		this.token = token;
		this.deliveryTime= deliveryTime;
		this.refund = false;
	}
 
	
	public static TransactionU createTransaction(String contextToPay,
			String paymentToPay, String paymentExecutionToPay,
			int userToPayId, int cartToPayId, Restaurant restaurantToPay, String token, int deliveryTime) {
 
		TransactionU transaction = new TransactionU(contextToPay, paymentToPay,
				paymentExecutionToPay, userToPayId, cartToPayId, restaurantToPay, token, deliveryTime);
		
		transaction.save();
		fillItems(transaction);
 
		return transaction;
	}
	
	
	private static void fillItems(TransactionU transaction) {
			Cart cart = Cart.find(transaction.cartToPayId);
		
		for(int i=0; i<cart.cartItems.size(); i++) {
			String name = cart.cartItems.get(i).getMealName(); 
			double price = cart.cartItems.get(i).meal.price; 
			int quantity = cart.cartItems.get(i).quantity;
			double totalPrice = cart.cartItems.get(i).totalPrice;
			MetaItem meta = new MetaItem(name, price, quantity, totalPrice, transaction);
			transaction.items.add(meta);
		} 
		
		List<Jsoner> metaitems = new ArrayList<Jsoner>();
		for(int i=0; i<transaction.items.size(); i++) {
			String name = transaction.items.get(i).name; 
			double price = transaction.items.get(i).price; 
			int quantity = transaction.items.get(i).quantity;
			double totalPrice = transaction.items.get(i).totalPrice;
			Jsoner jsoner = new Jsoner(name, price, quantity, totalPrice);
			metaitems.add(jsoner);
		}
 
		JsonNode jsonNode = Json.toJson(metaitems);
		Logger.debug("JSON I TO: " + jsonNode.toString());
		
		transaction.save();
	}
 
 
	public static TransactionU find(long id) {
		return find.byId(id);
	}
	
	public static List<TransactionU> all(){
		return find.all();
	}
	
	public static TransactionU findByCart(int cartId){
		return find.where().eq("cart_to_pay_id", cartId).findUnique();
	}
	
	public static List <TransactionU> findByUser(int id){
		return find.where().eq("user_to_pay_id", id).findList();
	}
	public static List <TransactionU> transactionsForRefund(){
		return find.where().eq("refund", true).findList();
	}
	
	public static boolean checkOrderTimeGap(TransactionU transaction){
		Date timeNow = new Date();
		long timeDiffernece = transaction.orderTimeGap  - timeNow.getTime();
		System.out.println("Time difference: " + timeDiffernece);
		if( timeDiffernece < 0 )
			return true;
		return false;
	}
	
}