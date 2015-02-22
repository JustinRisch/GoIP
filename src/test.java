import Encryption.Encryption;

public class test {
	static long clock = 0;
	public static void main(String[] args) {
		String testString = "Alan Mathison Turing, OBE, FRS was a British pioneering computer scientist, mathematician, logician, cryptanalyst, philosopher, mathematical biologist, and marathon and ultra distance runner. He was highly influential in the development of computer science, providing a formalisation of the concepts of algorithm and computation with the Turing machine, which can be considered a model of a general purpose computer.[3][4][5] Turing is widely considered to be the father of theoretical computer science and artificial intelligence.[6] During the Second World War, Turing worked for the Government Code and Cypher School (GC&CS) at Bletchley Park, Britain's codebreaking centre. For a time he led Hut 8, the section responsible for German naval cryptanalysis. He devised a number of techniques for breaking German ciphers, including improvements to the pre-war Polish bombe method, an electromechanical machine that could find settings for the Enigma machine. Turing's pivotal role in cracking intercepted coded messages enabled the Allies to defeat the Nazis in many crucial engagements, including the Battle of the Atlantic; it has been estimated that the work at Bletchley Park shortened the war in Europe by as many as two to four years.[7] After the war, he worked at the National Physical Laboratory, where he designed the ACE, among the first designs for a stored-program computer. In 1948 Turing joined Max Newman's Computing Laboratory at Manchester University, where he helped develop the Manchester computers[8] and became interested in mathematical biology. He wrote a paper on the chemical basis of morphogenesis, and predicted oscillating chemical reactions such as the Belousov–Zhabotinsky reaction, first observed in the 1960s.";//Turing was prosecuted in 1952 for homosexual acts, when such behaviour was still criminalised in the UK. He accepted treatment with oestrogen injections (chemical castration) as an alternative to prison. Turing died in 1954, 16 days before his 42nd birthday, from cyanide poisoning. An inquest determined his death a suicide, but it has since been noted that the known evidence is equally consistent with accidental poisoning.[9] In 2009, following an Internet campaign, British Prime Minister Gordon Brown made an official public apology on behalf of the British government for the appalling way he was treated. Queen Elizabeth II granted him a posthumous pardon in 2013.[10][11][12]";
		mark();
		testString=Encryption.superEncrypt(testString);
		mark();
		System.out.println(clock);
		System.out.println(testString);
		mark();
		testString=Encryption.superDecrypt(testString);
		mark();
		System.out.println(clock);
		System.out.println(testString);
		
	}
	static void mark(){
		clock=System.nanoTime()-clock;
	}
	
}
