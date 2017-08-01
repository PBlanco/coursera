import java.util.ArrayList;
import java.util.List;

public class TxHandler {

	private UTXOPool utxoPool;
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
    		 this.utxoPool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * 	- how do we know claimed?
     * (2) the signatures on each input of {@code tx} are valid, 
    	 * 	- Valid means verifySignatures() called.. pub key matches private
     * (3) no UTXO is claimed multiple times by {@code tx},
     *  - Keep track of UTXO that are claimed, and return false if double claimed
     * (4) all of {@code tx}s output values are non-negative, and
     *  - Loop through final outputs and make sure they are postive
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
    		double totalInputSum = 0;
    		double totalOutputSum = 0;
    		UTXOPool spentUtxoPool = new UTXOPool();
    		
    		for(int i = 0; i < tx.numInputs(); i++) {
    			Transaction.Input input = tx.getInput(i);
    			// a utxo that is in the pool and we would like to use as the input to this transaction
    			UTXO spenderUtxo = new UTXO(input.prevTxHash, input.outputIndex);
    			// get the utxo from the pool
    			Transaction.Output spenderOutput = this.utxoPool.getTxOutput(spenderUtxo);
    			// if the utxo doesn't exist, then the money doesn't exist, so the transaction is invalid.
    			if (spenderOutput == null) {
    				return false;
    			}
    			// avoid double spending by checking that we have not used this utxo already
    			if (spentUtxoPool.contains(spenderUtxo)) {
    				return false;
    			}
    			
    			// each input must be signed with the signature of its corresponding spent output
    			if (!Crypto.verifySignature(spenderOutput.address, tx.getRawDataToSign(i), input.signature)) {
    				return false;
    			}
    			
    			// add utxo value to total input value 
    			totalInputSum += spenderOutput.value;
    			// mark utxo as spent
    			spentUtxoPool.addUTXO(spenderUtxo, spenderOutput);
    		}
    		
    		for (Transaction.Output output: tx.getOutputs()) {
    			// output value must be non-negative
    			if (output.value < 0) {
    				return false;
    			}
    			// add output value to total output value
    			totalOutputSum += output.value;
    		}

    		// Must have more money in than out (can't generate money in trx)
    		// Any leftover money may be claimed as a transaction fee by the miner 
    		return totalInputSum >= totalOutputSum;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {	
    		List<Transaction> validTxs = new ArrayList<Transaction>();

    		for (Transaction tx : possibleTxs) {
    			if (!this.isValidTx(tx)) {
    				// Normally, if there are multiple txs using same utxo spender coins, the miner would accept the
    				// one that has a higher fee associated (extra credit - Did not implement).
    				continue;
    			}
    			
    			// Remove all input utxos since they are now spent
    			for (Transaction.Input input: tx.getInputs()) {
        			UTXO spenderUtxo = new UTXO(input.prevTxHash, input.outputIndex);
        			this.utxoPool.removeUTXO(spenderUtxo);
    			}
    			
    			// Add all output utxos since they are now confirmed unspent transaction outputs
        		for(int i = 0; i < tx.numOutputs(); i++) {
        			Transaction.Output output = tx.getOutput(i);
        			UTXO receiverUtxo = new UTXO(tx.getHash(), i);
        			this.utxoPool.addUTXO(receiverUtxo, output);
        		}
    
    			validTxs.add(tx);
    		}
    		
    		return validTxs.toArray(new Transaction[validTxs.size()]);
    }

}
