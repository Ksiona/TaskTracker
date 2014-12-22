package commonResources.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Shmoylova Kseniya
 * POJO for entity "Activity type"
 * Annotated for JAXB serialization
 */
@XmlRootElement()
@XmlType(propOrder={FieldNames.ACTIVITY_TYPE_NOTES, FieldNames.ACTIVITY_TYPE})
public class ActivityType implements Serializable {

	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private String owner;
	private String activityTypeTitle;
	private int activityTypeID;
	private String activityTypeNotes;
	private List<ActivityType> activityType = new ArrayList<>();	
	
	public ActivityType() {
	}
	
	public ActivityType(String activityTypeTitle, int activityTypeID) {
		super();
		this.activityTypeTitle = activityTypeTitle;
		this.activityTypeID = activityTypeID;
	}
	
	public void appendChild(ActivityType newActivityType){
		this.activityType.add(newActivityType);
	}
	
	public ActivityType getChild(int activityTypeID){
		return activityType.get(activityTypeID);
	}
	@XmlAttribute
	public String getActivityTypeTitle() {
		return activityTypeTitle;
	}
	public void setActivityTypeTitle(String activityTypeTitle) {
		this.activityTypeTitle = activityTypeTitle;
	}
	@XmlAttribute
	public int getActivityTypeID() {
		return activityTypeID;
	}
	public void setActivityTypeID(int activityTypeID) {
		this.activityTypeID = activityTypeID;
	}
	public List<ActivityType> getActivityType() {
		return activityType;
	}
	public void setActivityType(List<ActivityType> activityType) {
		this.activityType = activityType;
	}
	public String getActivityTypeNotes() {
		return activityTypeNotes;
	}
	public void setActivityTypeNotes(String activityTypeNotes) {
		this.activityTypeNotes = activityTypeNotes;
	}
	@XmlAttribute
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return getActivityTypeTitle();
	}
}
