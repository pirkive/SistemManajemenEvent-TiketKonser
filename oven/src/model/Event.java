package model;

public class Event {
    private String eventId;
    private String title;
    private int quota;
    private double price;

    public Event(String eventId, String title, int quota, double price) {
        this.eventId = eventId;
        this.title = title;
        this.quota = quota;
        this.price = price;
    }

    // Getter & Setter
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getQuota() { return quota; }
    public void setQuota(int quota) { 
        // Logika Enkapsulasi: Stok tidak boleh negatif
        if (quota >= 0) {
            this.quota = quota;
        } else {
            System.out.println("Error: Kuota tidak valid!");
        }
    }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}