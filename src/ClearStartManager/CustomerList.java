package ClearStartManager;

import java.util.List;

class CustomerList {
    private List<Customer> customers;

    CustomerList(List<Customer> customers) {
        this.customers = customers;
    }

    List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    Customer getCustomerByIndex(int index) throws IndexOutOfBoundsException {
        Customer customer = this.customers.get(index);
        if (customer != null) {
            return customer;
        }
        throw new IndexOutOfBoundsException();
    }

    void addCustomer(String name, List<Setting> settings) {
        Customer customer = new Customer(name, settings);
        customers.add(customer);
    }

}
