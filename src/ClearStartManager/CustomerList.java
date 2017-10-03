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

    Customer getCustomerByIndex(int index) throws NoSuchElementException {
        Customer customer = this.customers.get(index);
        if (customer != null) {
            return customer;
        }
        throw new NoSuchElementException();
    }

}
