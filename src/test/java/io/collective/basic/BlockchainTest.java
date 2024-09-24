package io.collective.basic;

import org.junit.Before;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import static org.junit.Assert.*;

public class BlockchainTest {
    private Blockchain emptyBlockchain;
    private Blockchain singleBlockBlockchain;
    private Blockchain multiBlockBlockchain;

    @Before
    public void setUp() throws NoSuchAlgorithmException {
        // Setup blockchains
        emptyBlockchain = new Blockchain();
        singleBlockBlockchain = new Blockchain();
        multiBlockBlockchain = new Blockchain();

        // Create the genesis block and mine it
        Block genesis = Blockchain.mine(new Block("0", Instant.now().getEpochSecond(), 0));
        singleBlockBlockchain.add(genesis);

        // Add multiple blocks to the blockchain
        Block secondBlock = Blockchain.mine(new Block(genesis.getHash(), Instant.now().getEpochSecond(), 0));
        multiBlockBlockchain.add(genesis);
        multiBlockBlockchain.add(secondBlock);
    }

    @Test
    public void testIsEmpty() {
        // Test that the empty blockchain is empty
        assertTrue("Blockchain should be empty", emptyBlockchain.isEmpty());

        // Test that the non-empty blockchains are not empty
        assertFalse("Blockchain with one block should not be empty", singleBlockBlockchain.isEmpty());
        assertFalse("Blockchain with multiple blocks should not be empty", multiBlockBlockchain.isEmpty());
    }

    @Test
    public void testSize() {
        // Test size of blockchains
        assertEquals("Empty blockchain should have size 0", 0, emptyBlockchain.size());
        assertEquals("Blockchain with one block should have size 1", 1, singleBlockBlockchain.size());
        assertEquals("Blockchain with multiple blocks should have size 2", 2, multiBlockBlockchain.size());
    }

    @Test
    public void testAddBlock() throws NoSuchAlgorithmException {
        // Add a block to an empty blockchain
        Block newBlock = Blockchain.mine(new Block("0", Instant.now().getEpochSecond(), 0));
        emptyBlockchain.add(newBlock);

        // Ensure the block was added correctly
        assertEquals("Blockchain size should be 1 after adding a block", 1, emptyBlockchain.size());
        assertFalse("Blockchain should not be empty after adding a block", emptyBlockchain.isEmpty());
    }

    @Test
    public void testBlockchainValidity() throws NoSuchAlgorithmException {
        // Test that an empty blockchain is valid
        assertTrue("Empty blockchain should be valid", emptyBlockchain.isValid());

        // Test that a single block blockchain is valid
        assertTrue("Blockchain with one block should be valid", singleBlockBlockchain.isValid());

        // Test that a multi-block blockchain is valid
        assertTrue("Blockchain with multiple blocks should be valid", multiBlockBlockchain.isValid());
    }

    @Test
    public void testIsNotValid_WhenBlockIsNotMined() throws NoSuchAlgorithmException {
        // Create an unmined block (without the "00" hash condition)
        Block unminedBlock = new Block("0", Instant.now().getEpochSecond(), 0);

        Blockchain invalidBlockchain = new Blockchain();
        invalidBlockchain.add(unminedBlock);

        // Ensure that the blockchain is invalid
        assertFalse("Blockchain with an unmined block should be invalid", invalidBlockchain.isValid());
    }

    @Test
    public void testIsNotValid_WhenPreviousHashDoesNotMatch() throws NoSuchAlgorithmException {
        // Mine two blocks but tamper with the second block's previous hash
        Block genesis = Blockchain.mine(new Block("0", Instant.now().getEpochSecond(), 0));
        Block invalidSecondBlock = Blockchain.mine(new Block("tamperedHash", Instant.now().getEpochSecond(), 0));

        Blockchain invalidBlockchain = new Blockchain();
        invalidBlockchain.add(genesis);
        invalidBlockchain.add(invalidSecondBlock);

        // Ensure the blockchain is invalid
        assertFalse("Blockchain with mismatched previous hashes should be invalid", invalidBlockchain.isValid());
    }

    @Test
    public void testMineBlock() throws NoSuchAlgorithmException {
        // Create a block and mine it
        Block block = new Block("0", Instant.now().getEpochSecond(), 0);
        Block minedBlock = Blockchain.mine(block);

        // Ensure that the block is mined correctly
        assertTrue("Mined block should have a valid hash", Blockchain.isMined(minedBlock));
    }

    @Test
    public void testIsMined() throws NoSuchAlgorithmException {
        // Create a block and mine it
        Block block = new Block("0", Instant.now().getEpochSecond(), 0);
        Block minedBlock = Blockchain.mine(block);

        // Ensure the block was mined successfully (i.e., hash starts with "00")
        assertTrue("Block should be mined when its hash starts with '00'", Blockchain.isMined(minedBlock));
    }

    @Test
    public void testTamperedHash_MakesBlockchainInvalid() throws NoSuchAlgorithmException, NoSuchFieldException, IllegalAccessException {
        Blockchain blockchain = new Blockchain();
        Block minedBlock = Blockchain.mine(new Block("0", Instant.now().getEpochSecond(), 0));
        
        // Tamper with the hash of the mined block using reflection
        java.lang.reflect.Field hashField = minedBlock.getClass().getDeclaredField("hash");
        hashField.setAccessible(true);
        hashField.set(minedBlock, "tamperedHash");

        blockchain.add(minedBlock);

        // Ensure that the blockchain is invalid due to tampered hash
        assertFalse("Blockchain should be invalid when a block's hash is tampered", blockchain.isValid());
    }
}
