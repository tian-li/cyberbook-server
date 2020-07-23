package app.cyberbook.cyberbookserver.model;

public class Category {
//    id: string;
//    userId: string;
//    sortOrder: number;
//    name: string;
//    icon: string;
//    color: string;
//    type: TransactionType;
//    addedByUser: boolean;

    String id;
    String userId;
    Integer sortOrder;
    String name;
    String icon;
    String color;
    Const.TransactionType type;
    Boolean addedByUser;

    public Category(String id, String userId, Integer sortOrder, String name, String icon, String color, Const.TransactionType type, Boolean addedByUser) {
        this.id = id;
        this.userId = userId;
        this.sortOrder = sortOrder;
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.type = type;
        this.addedByUser = addedByUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Const.TransactionType getType() {
        return type;
    }

    public void setType(Const.TransactionType type) {
        this.type = type;
    }

    public Boolean getAddedByUser() {
        return addedByUser;
    }

    public void setAddedByUser(Boolean addedByUser) {
        this.addedByUser = addedByUser;
    }
}
