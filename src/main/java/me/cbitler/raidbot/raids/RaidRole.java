package me.cbitler.raidbot.raids;

/**
 * Represents a role that is available in a raid
 * @author Christopher Bitler
 */
public class RaidRole {
    int amount;
    String name;
    boolean flexOnly;

    /**
     * Create a new RaidRole object
     * @param amount The max amount of the role
     * @param name The name of the role
     */
    public RaidRole(int amount, String name) {
        this.flexOnly = false;
        if(name.startsWith("!")){
            name = name.substring(1);
            this.flexOnly = true;
        }
        if(amount==0){
            this.flexOnly = true;
        }
        this.amount = amount;
        this.name = name;
    }

    /**
     * Get the maximum number of people in this role
     * @return The maximum number of people in this role
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Get the name of the role
     * @return The name of the role
     */
    public String getName() {
        return name;
    }

    /**
     * Set the amount of the role
     * @param newamount The new amount of the role
     */
    public void setAmount(int newamount) {
        amount = newamount;
    }

    /**
     * Set the name of the role
     * @param newname The new name of the role
     */
    public void setName(String newname) {
        name = newname;
    }

    /**
     * Set the role to be flex only
     * @param makeFlexOnly Whether this role should be flex only
     */
    public void setFlexOnly(boolean makeFlexOnly){
        this.flexOnly = makeFlexOnly;
    }

    /**
     * Get whether this role is flex only
     * @return Whether this role is flex only
     */
    public boolean isFlexOnly(){
        return this.flexOnly;
    }
}
