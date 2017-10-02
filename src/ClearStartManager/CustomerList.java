package ClearStartManager;

import java.util.List;
import java.util.NoSuchElementException;

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

    Customer getCustomerByName(String name) throws NoSuchElementException {
        for (Customer customer : this.customers) {
            if (name.equals(customer.getName())) {
                return customer;
            }
        }
        throw new NoSuchElementException();
    }

}
