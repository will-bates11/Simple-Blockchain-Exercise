package io.collective.basic;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Blockchain {
    private final List<Block> chain;

    // Constructor to initialize the blockchain
    public Blockchain() {
        this.chain = new ArrayList<>();
    }

    // Check if the blockchain is empty
    public boolean isEmpty() {
        return chain.isEmpty();
    }

    // Add a block to the blockchain after validating it
    public void add(Block block) throws NoSuchAlgorithmException {
        if (chain.isEmpty() || (isValidBlock(block) && block.getPreviousHash().equals(getLatestBlock().getHash()))) {
            chain.add(block);
        } else {
            throw new IllegalArgumentException("Block is invalid or does not match the previous hash.");
        }
    }

    // Get the size of the blockchain
    public int size() {
        return chain.size();
    }

    // Validate the entire blockchain
    public boolean isValid() throws NoSuchAlgorithmException {
        // Check if the chain is empty or has only one block (valid by definition)
        if (chain.size() <= 1) {
            return true;
        }

        // Iterate through the blockchain and check for validity of each block
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            // Check if the previous hash matches the hash of the previous block
            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                return false;
            }

            // Ensure the current block is mined
            if (!isMined(currentBlock)) {
                return false;
            }

            // Ensure the hash of the current block is valid (i.e., correctly calculated)
            if (!currentBlock.getHash().equals(currentBlock.calculatedHash())) {
                return false;
            }
        }

        return true;
    }

    // Helper function to get the latest block in the blockchain
    private Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    // Validate a single block before adding it to the blockchain
    private boolean isValidBlock(Block block) throws NoSuchAlgorithmException {
        return isMined(block) && block.getHash().equals(block.calculatedHash());
    }

    /// Supporting functions that you'll need.

    // Mining function that iterates over different nonce values to mine a block
    public static Block mine(Block block) throws NoSuchAlgorithmException {
        Block mined = new Block(block.getPreviousHash(), block.getTimestamp(), block.getNonce());

        // Iterate until a mined block is found (where the hash starts with "00")
        while (!isMined(mined)) {
            mined = new Block(mined.getPreviousHash(), mined.getTimestamp(), mined.getNonce() + 1);
        }
        return mined;
    }

    // Check if the block has been mined, i.e., if its hash starts with "00"
    public static boolean isMined(Block minedBlock) {
        return minedBlock.getHash().startsWith("00");
    }
}
