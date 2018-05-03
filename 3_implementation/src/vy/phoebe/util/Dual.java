/**
 * 
 */
package vy.phoebe.util;



/**
 * @author Loc Nguyen
 * @version 2.0
 *
 */
public class Dual {

	/**
	 * 
	 */
	protected double one_ = Constants.UNUSED;
	
	
	/**
	 * 
	 */
	protected double other_ = Constants.UNUSED;
	
	
	/**
	 * 
	 * @param one
	 * @param other
	 */
	public Dual(double one, double other) {
		this.one_ = one;
		this.other_ = other;
	}
	
	
	/**
	 * 
	 * @return one value
	 */
	public double one() {
		return one_;
	}
	
	
	/**
	 * 
	 * @return other value
	 */
	public double other() {
		return other_;
	}
	
	
	/**
	 * 
	 * @param one
	 */
	public void setOne(double one) {
		this.one_ = one;
	}
	

	/**
	 * 
	 * @param other
	 */
	public void setOther(double other) {
		this.other_ = other;
	}
	
	
}
