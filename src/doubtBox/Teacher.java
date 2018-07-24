package doubtBox;
/**
* The teacher class holds the details of teacher.
* @author  Hitesh Yadav
* @since   2017-10-25 
*/

public class Teacher {
	private String strtName, strIP, strTime;
	/**
	 * This is a constructor of Teacher class which 
	 * assigns value to data members of this class.
	 * @param strtName
	 * @param strIP
	 * @param strTime
	 */
	public Teacher(String strtName, String strIP, String strTime) {
		this.strtName = strtName;
		this.strIP = strIP;
		this.strTime = strTime;
	}

		/**
		 * This is a getter method which gets the value of
		 * name of the teacher. 
		 * @return String the value of name in String format.
		 */
	public String getStrtName() {
		return strtName;
	}
	/**
	 * This is a setter method which gets the value of
	 * StrName method 
	 * @param strtName
	 * @return void this returns nothing.
	 */
	public void setStrtName(String strtName) {
		this.strtName = strtName;
	}

	/**
	 * This is a getter method which returns the value of
	 * IP of teacher.
	 * @return String the IP of teacher.
	 */
	public String getStrIP() {
		return strIP;
	}

		/**
		 *  This is a setter method which gets the value of 
		 *  StrIP method 
		 * @param strIP
		 * @return void this returns nothing.
		 */
	public void setStrIP(String strIP) {
		this.strIP = strIP;
	}

	/**
	 * This is a getter method which gets the value of 
	 * IP of the teacher.
	 * @return String the value of time.
	 */
	public String getStrTime() {
		return strTime;
	}

	/**
	 * This is a setter method which gets the value 
	 * of StrTime
	 * @param strTime
	 * @return void this returns nothing.
	 */
	public void setStrTime(String strTime) {
		this.strTime = strTime;
	}
	

}
