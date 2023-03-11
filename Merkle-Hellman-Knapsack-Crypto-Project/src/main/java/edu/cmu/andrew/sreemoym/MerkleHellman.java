// Name: Sreemoyee Mukherjee
// Course: Data Structures & Algorithms
// Assignment Number: 1

package edu.cmu.andrew.sreemoym;

import java.math.BigInteger;
import java.util.Scanner;

public class MerkleHellman {
    SinglyLinkedList w = new SinglyLinkedList();
    SinglyLinkedList b = new SinglyLinkedList();
    BigInteger q, r_inv;
    public static void main(String[] args) {
        MerkleHellman mh = new MerkleHellman();
        mh.keyGenerator();
        byte[] clearBytes;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter a string and I will encrypt it as single large integer.");
        String s = sc.nextLine();
        while (s.length() > 80) {
            System.out.println("Your entered string is too long, please try again: ");
            s = sc.nextLine();
        }
        System.out.println("Clear text: \n" + s);
        clearBytes = s.getBytes();
        System.out.println("Number of clear text bytes = " + clearBytes.length);
        BigInteger c = mh.encrypt(clearBytes);
        System.out.println("\n" + s + " is encrypted as " + c);
        byte[] decryptedBytes = mh.decrypt(c);
        String result = new String(decryptedBytes);

        result = result.trim();
        System.out.println("Result of decryption: " + result);
        sc.close();
    }

    public void keyGenerator() {
        int count = 1;
        while (count <= 640) {
            BigInteger b = new BigInteger("7");
            ;
            BigInteger i = b.pow(count);
            w.addAtEndNode(i);
            count++;
        }
        BigInteger sum = new BigInteger("0");
        BigInteger r;
        w.reset();
        while (w.hasNext()) {
            BigInteger i = (BigInteger) w.next();
            sum = sum.add(i);
        }
        q = sum.add(new BigInteger("100"));     // Added a constant 100 to ensure q is greater than sum of all w
        r = new BigInteger("1000");     // randomly starting searching for r from constant 1000
        while (!(q.gcd(r).equals(BigInteger.ONE))) {
            r = r.add(BigInteger.ONE);
        }
        r_inv = r.modInverse(q);
        w.reset();
        while (w.hasNext()) {
            BigInteger i = (BigInteger) w.next();
            BigInteger prod = r.multiply(i);
            BigInteger value = prod.mod(q);
            b.addAtEndNode(value);
        }
    }

    public BigInteger encrypt(byte[] cb) {
        SinglyLinkedList m_bin = new SinglyLinkedList();    // storing message in bits
        for(byte i: cb){
            char[] c_bin = byteToBitArray(i).toCharArray();
            for(char c: c_bin){
                m_bin.addAtEndNode(c);
            }

        }
        m_bin.reset();
        b.reset();
        BigInteger sum = new BigInteger("0");
        while (m_bin.hasNext()) {
            char c = (char) m_bin.next();
            BigInteger i = new BigInteger(String.valueOf(c));
            BigInteger prod = i.multiply((BigInteger) b.next());
            sum = sum.add(prod);
        }
        return sum;
    }

    /**
     * byteToBitArray() converts the byte message to bits
     **/
    String byteToBitArray(byte b) {
        String bits = Integer.toBinaryString(b & 0xFF);
        while (bits.length() < 8) {
            bits = "0" + bits;}
            return bits;
    }
    public byte[] decrypt(BigInteger c){
        BigInteger prod = c.multiply(r_inv);
        BigInteger c_dash = prod.mod(q);

        // solving the subset sum problem
        SinglyLinkedList x = new SinglyLinkedList();
        for (int i=w.countNodes - 1; i>=0; i--) {
            if (!(c_dash.compareTo((BigInteger) w.getObjectAt(i)) == -1) && (c_dash.compareTo(BigInteger.ZERO) == 1)) {
                c_dash = c_dash.subtract((BigInteger) w.getObjectAt(i));
                BigInteger index = BigInteger.valueOf(i).add(BigInteger.ONE);
                x.addAtEndNode(index);
            }
        }

        BigInteger sum = new BigInteger("0");
        x.reset();
        while(x.hasNext()){
            BigInteger val = (BigInteger) x.next();
            BigInteger exp_val = BigInteger.valueOf(2).pow(640 - val.intValue());
            sum = sum.add(exp_val);
        }
        return sum.toByteArray();
    }
}