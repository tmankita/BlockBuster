package bgu.spl181.net.json;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Movie {
	// local variables
	@SerializedName("id")
	@Expose
	private String id;
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("price")
	@Expose
	private String price;
	@SerializedName("bannedCountries")
	@Expose
	private List<String> bannedCountries = null;
	@SerializedName("availableAmount")
	@Expose
	private String availableAmount;
	@SerializedName("totalAmount")
	@Expose
	private String totalAmount;

	// constructor
	public Movie(String movieName, int totalAmount, int price, int id, List<String> banned) {
		this.name = movieName;
		this.totalAmount = (new Integer(totalAmount)).toString();
		this.availableAmount = this.totalAmount;
		this.price = (new Integer(price)).toString();
		this.id = (new Integer(id)).toString();
		this.bannedCountries = banned;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public List<String> getBannedCountries() {
		return bannedCountries;
	}

	public void setBannedCountries(List<String> bannedCountries) {
		this.bannedCountries = bannedCountries;
	}

	public String getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(String availableAmount) {
		this.availableAmount = availableAmount;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	/**
	 * Reduces the amount of the copies available
	 */
	public void reduceAmount() {
		int amountInt = Integer.parseInt(this.availableAmount);
		Integer newAmount = new Integer(amountInt - 1);
		this.availableAmount = newAmount.toString();
	}

	/**
	 * Increases amount of copies available
	 */
	public void increaseAmount() {	
		int amountInt = Integer.parseInt(this.availableAmount);
		Integer newAmount = new Integer(amountInt + 1);
		this.availableAmount = newAmount.toString();
	}

}
