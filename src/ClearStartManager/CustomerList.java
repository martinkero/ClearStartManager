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

    void addCustomer(Customer customer) {
        customers.add(customer);
    }

    void deleteCustomer(Customer customer) {
        customers.remove(customer);
    }

    void sort() {
        customers.sort((o1, o2) -> {
            if ("default".equalsIgnoreCase(o1.getName())) {
                return -1;
            }
            if ("default".equalsIgnoreCase(o2.getName())) {
                return 1;
            }
            return o1.getName().compareToIgnoreCase(o2.getName());
        });
    }

}
