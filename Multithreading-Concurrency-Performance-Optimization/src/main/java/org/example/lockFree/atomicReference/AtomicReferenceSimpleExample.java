package org.example.lockFree.atomicReference;


import java.util.concurrent.atomic.AtomicReference;

//Example of AtomicReference implementation
//AtomisReference is an object reference which allows us to use atomic operations such as
//compareAndSet which check if expectedValue return true, then updating it to new value like in example below
public class AtomicReferenceSimpleExample {
    public static void main(String[] args) {
        String oldName = "oldName";
        String newName = "newName";
        AtomicReference<String> atomicReference = new AtomicReference<>(oldName);

        if (atomicReference.compareAndSet(oldName, newName)) {
            System.out.println(atomicReference.get());
        } else {
            System.out.println("Name is different");
        }

    }
}
