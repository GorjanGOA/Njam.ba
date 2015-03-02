package models;

import java.util.List;

import javax.persistence.*;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Restaurant extends Model{

	@Id
	@PrimaryKeyJoinColumn
	public int id;
	@Required
	@Column(unique = true)	
	public String email;
	@Required
	public String hashedPassword;
	
	@Required
	@Column(unique = true)
	public boolean isRestaurant;
	
	@OneToOne
	public Location location;
	
	
	static Finder<Integer, Restaurant> find =  new Finder<Integer,Restaurant>(Integer.class, Restaurant.class);
	
	public Restaurant(String email, String hashedPassword){
		this.email = email;
		this.hashedPassword = hashedPassword;
		// TODO hash Password
	}
	
	public static  void create(String email, String hashedPassword){
		new Restaurant(email, hashedPassword);
	}
	
	public static Restaurant find(int id){
		return find.byId(id);
	}
		
	public static void delete(int id){
		find.byId(id);
	}
	public static List<Restaurant> all(String hashedPassword){
		return find.where().eq("hashedPassword", hashedPassword).findList();
	}
}
