import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
		// Your code goes here
        In file = new In(fileName);
        String window = "";
        for( int i =0 ; i < windowLength; i++)
        {
                window = window + file.readChar();
        }
        while (!file.isEmpty()) 
        {
         char tempc = file.readChar();
         List x = CharDataMap.get(window);
         if (x == null)
         {
            x = new List();
            CharDataMap.put(window, x);
         }
        x.update(tempc); //add the tempc to the list or update the count
        window = window.substring(1) + (tempc); // we cut the beginning and than add the char - this is the new window
         

        }
        for ( List probs : CharDataMap.values()) //for each value in the CharDataMap we calculate the p and cp of the list
        {
            calculateProbabilities(probs);
        }
    }
       
	
    

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	public void calculateProbabilities(List probs) {				
		// Your code goes here
        int charsum =0;
        double cphelper = 0;
        //take the list and sum up how many chars we have in order to calculate p and cp
        for(int i = 0; i < probs.getSize(); i++)
        {
            CharData temp = probs.get(i);
            charsum = charsum + temp.count;
        }
        //calculate p and cp
        for(int j=0 ; j < probs.getSize(); j++)
        {
            CharData temp = probs.get(j);
            temp.p = (double) temp.count / charsum; //put the p value of each char by taking the count of exist and divison of total numchars
            temp.cp = (double) (cphelper + temp.p);
            cphelper = cphelper + temp.p;
        }
	}

    // Returns a random character from the given probabilities list.
    
	public char getRandomChar(List probs) {
		// Your code goes here
        double r = randomGenerator.nextDouble();
        for(int i = 0; i < probs.getSize(); i++)
        {
            if(probs.get(i).cp > r)
            {
                return probs.get(i).chr;
            }
        }
        return probs.get(probs.getSize()-1).chr; //in ideal world this command will never happened
	}
    

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */

     
	public String generate(String initialText, int textLength) {
		// Your code goes here
        if(windowLength > initialText.length())
        {
            return initialText;
        }
        String window = initialText.substring(initialText.length()- windowLength); //make the window
        String generated = window;
        while (generated.length() < (textLength + windowLength))
        {
            List probList = CharDataMap.get(window);
            if(probList == null) // if the 
            {
                return initialText;
            }
            char tempRandomChar = getRandomChar(probList); //get the random char that depend of p 
            generated = generated + tempRandomChar; //add the random char to the result we want
            window += tempRandomChar; //add one char to window
            window = window.substring(1); //remove the first char so we just move the window one step forward
        }


        return generated;
	}


    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
		// Your code goes here
        int windowLength = Integer.parseInt(args[0]);
        String intialText = args[1];
        int generatedTextLength = Integer.parseInt(args[2]);
        Boolean randomGeneration = args[3].equals("random");
        String filename = args[4];

        LanguageModel lm;
        if (randomGeneration)
        {
            lm =new LanguageModel(windowLength);
        }
        else
        {
            lm = new LanguageModel(windowLength, 20);
        
        }

        lm.train(filename);
        System.out.println(lm.generate(intialText, generatedTextLength));
    }


        /* 
        String test = " eettimmoc";
        List tlist = new List();
        for(int i = 0; i < test.length(); i++)
        {
            tlist.update(test.charAt(i));
        }
        LanguageModel a = new LanguageModel(0);
        a.calculateProbabilities(tlist);
        System.out.println(a);

    }
    */
}
