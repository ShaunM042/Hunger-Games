package games;

import java.util.ArrayList;

/**
 * This class contains methods to represent the Hunger Games using BSTs.
 * Moves people from input files to districts, eliminates people from the game,
 * and determines a possible winner.
 * 
 * @author Pranay Roni
 * @author Maksims Kurjanovics Kravcenko
 * @author Kal Pandit
 */
public class HungerGames {

    private ArrayList<District> districts; // all districts in Panem.
    private TreeNode game; // root of the BST. The BST contains districts that are still in the game.

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * Default constructor, initializes a list of districts.
     */
    public HungerGames() {
        districts = new ArrayList<>();
        game = null;
        StdRandom.setSeed(2023);
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * Sets up Panem, the universe in which the Hunger Games takes place.
     * Reads districts and people from the input file.
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupPanem(String filename) {
        StdIn.setFile(filename); // open the file - happens only once here
        setupDistricts(filename);
        setupPeople(filename);
    }

    /**
     * Reads the following from input file:
     * - Number of districts
     * - District ID's (insert in order of insertion)
     * Insert districts into the districts ArrayList in order of appearance.
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupDistricts(String filename) {
        StdIn.setFile(filename);
        int numberofDistricts = Integer.valueOf(StdIn.readLine());
        for (int x = 0; x < numberofDistricts; x++) {
            District newDistrict = new District(Integer.valueOf(StdIn.readLine()));
            districts.add(x, newDistrict);
        }
    }

    /**
     * Reads the following from input file (continues to read from the SAME input
     * file as setupDistricts()):
     * Number of people
     * Space-separated: first name, last name, birth month (1-12), age, district id,
     * effectiveness
     * Districts will be initialized to the instance variable districts
     * 
     * Persons will be added to corresponding district in districts defined by
     * districtID
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupPeople(String filename) {

        StdIn.setFile(filename);
        int numberofDistricts = Integer.valueOf(StdIn.readLine());
        int[] valueofDistrict = new int[numberofDistricts];
        for (int x = 0; x < numberofDistricts; x++) {
            valueofDistrict[x] = Integer.valueOf(StdIn.readLine());
        }
        String[] dataofPeople = StdIn.readAllLines();
        int numberofPeople = Integer.valueOf(dataofPeople[0]);
        for (int x = 1; x <= numberofPeople; x++) {
            String personData[] = dataofPeople[x].split(" ");
            Person newPerson = new Person(Integer.valueOf(personData[2]), personData[0], personData[1],
                    Integer.valueOf(personData[3]), Integer.valueOf(personData[4]), Integer.valueOf(personData[5]));
            if (Integer.valueOf(personData[3]) >= 12 && Integer.valueOf(personData[3]) < 18)
                newPerson.setTessera(true);
            else
                newPerson.setTessera(false);
            for (int y = 0; y < numberofDistricts; y++) {
                if (districts.get(y).getDistrictID() == Integer.valueOf(personData[4])) {
                    if (Integer.valueOf(personData[2]) % 2 == 0)
                        districts.get(y).addEvenPerson(newPerson);
                    else
                        districts.get(y).addOddPerson(newPerson);
                }
            }
        }
    }

    /**
     * Adds a district to the game BST.
     * If the district is already added, do nothing
     * 
     * @param root        the TreeNode root which we access all the added districts
     * @param newDistrict the district we wish to add
     */
    public void addDistrictToGame(TreeNode root, District newDistrict) {
        if (root == null) {
            game = new TreeNode(newDistrict, null, null);
            districts.remove(newDistrict);
        } else {
            addDistrictToGameHelper(root, newDistrict);
        }
    }

    private void addDistrictToGameHelper(TreeNode root, District newDistrict) {
        if (newDistrict.getDistrictID() < root.getDistrict().getDistrictID()) {
            if (root.getLeft() == null) {
                root.setLeft(new TreeNode(newDistrict, null, null));
                districts.remove(newDistrict);
            } else {
                addDistrictToGameHelper(root.getLeft(), newDistrict);
            }
        } else if (newDistrict.getDistrictID() > root.getDistrict().getDistrictID()) {
            if (root.getRight() == null) {
                root.setRight(new TreeNode(newDistrict, null, null));
                districts.remove(newDistrict);
            } else {
                addDistrictToGameHelper(root.getRight(), newDistrict);
            }
        }
    }

    /**
     * Searches for a district inside of the BST given the district id.
     * 
     * @param id the district to search
     * @return the district if found, null if not found
     */
    public District findDistrict(int id) {
        TreeNode pointer = game;
        return findDistrict(pointer, id);
    }

    private District findDistrict(TreeNode gameNode, int id) {
        if (gameNode == null) {
            return null;
        }

        int currentId = gameNode.getDistrict().getDistrictID();

        if (id == currentId) {
            return gameNode.getDistrict();
        } else if (id < currentId) {
            return findDistrict(gameNode.getLeft(), id);
        } else {
            return findDistrict(gameNode.getRight(), id);
        }
    }

    /**
     * Selects two duelers from the tree, following these rules:
     * - One odd person and one even person should be in the pair.
     * - Dueler with Tessera (age 12-18, use tessera instance variable) must be
     * retrieved first.
     * - Find the first odd person and even person (separately) with Tessera if they
     * exist.
     * - If you can't find a person, use StdRandom.uniform(x) where x is the
     * respective
     * population size to obtain a dueler.
     * - Add odd person dueler to person1 of new DuelerPair and even person dueler
     * to
     * person2.
     * - People from the same district cannot fight against each other.
     * 
     * @return the pair of dueler retrieved from this method.
     */
    public DuelPair selectDuelers() {

        Person person1 = odd(game, -1);
        Person person2;

        if (person1 == null) {
            person2 = even(game, -1);
        } else {
            person2 = even(game, person1.getDistrictID());
        }

        if (person1 == null) {
            if (person2 == null) {
                person1 = randomOdd(game, -1);
            } else {
                person1 = randomOdd(game, person2.getDistrictID());
            }
        }

        if (person2 == null) {
            if (person1 == null) {
                person2 = randomEven(game, -1);
            } else {
                person2 = randomEven(game, person1.getDistrictID());
            }
        }

        DuelPair pairofpeople = new DuelPair(person1, person2);

        if (person1 != null) {

            District Districtone = findDistrict(person1.getDistrictID());
            ArrayList<Person> oddperson = Districtone.getOddPopulation();
            oddperson.remove(person1);
        }
        if (person2 != null) {
            District twoDist = findDistrict(person2.getDistrictID());
            ArrayList<Person> evenperson = twoDist.getEvenPopulation();
            evenperson.remove(person2);
        }

        return pairofpeople;
    }

    private Person odd(TreeNode x, int id) {

        if (x == null) {
            return null;
        }

        ArrayList<Person> odd = x.getDistrict().getOddPopulation();
        for (int i = 0; i < odd.size(); i++) {
            if (odd.get(i).getTessera() == true) {
                if (x.getDistrict().getDistrictID() != id)
                    return odd.get(i);
            }
        }
        Person next = odd(x.getLeft(), id);
        if (next != null) {
            return next;
        }
        Person nextnext = odd(x.getRight(), id);
        if (nextnext != null) {
            return nextnext;
        }

        return null;
    }

    private Person even(TreeNode x, int id) {

        if (x == null) {
            return null;
        }

        ArrayList<Person> even = x.getDistrict().getEvenPopulation();
        for (int i = 0; i < even.size(); i++) {
            if (even.get(i).getTessera() == true) {
                if (x.getDistrict().getDistrictID() != id)
                    return even.get(i);
            }
        }
        Person next = even(x.getLeft(), id);
        if (next != null) {
            return next;
        }
        Person nextnext = even(x.getRight(), id);
        if (nextnext != null) {
            return nextnext;
        }

        return null;

    }

    private Person randomOdd(TreeNode x, int id) {

        if (x == null) {
            return null;
        }

        ArrayList<Person> odd = x.getDistrict().getOddPopulation();

        int thing = StdRandom.uniform(odd.size());
        Person randomperson = odd.get(thing);

        if (randomperson.getDistrictID() == id) {
            Person next = randomOdd(x.getLeft(), id);
            if (next != null) {
                return next;
            }

            Person nextnext = randomOdd(x.getRight(), id);
            if (nextnext != null) {
                return nextnext;
            }
        } else {
            return randomperson;
        }

        return null;

    }

    private Person randomEven(TreeNode x, int id) {

        if (x == null) {
            return null;
        }

        ArrayList<Person> evennumber = x.getDistrict().getEvenPopulation();

        int thing = StdRandom.uniform(evennumber.size());
        Person randomperson = evennumber.get(thing);

        if (randomperson.getDistrictID() == id) {
            Person next = randomEven(x.getLeft(), id);
            if (next != null) {
                return next;
            }

            Person nextnextperson = randomEven(x.getRight(), id);
            if (nextnextperson != null) {
                return nextnextperson;
            }
        } else {
            return randomperson;

        }

        return null;
    }

    /**
     * Deletes a district from the BST when they are eliminated from the game.
     * Districts are identified by id's.
     * If district does not exist, do nothing.
     * 
     * This is similar to the BST delete we have seen in class.
     * 
     * @param id the ID of the district to eliminate
     */
    public void eliminateDistrict(int id) {
        game = deleteDistrict(game, id);
    }

    private TreeNode deleteDistrict(TreeNode root, int id) {
        if (root == null) {
            return root;
        }

        int rootID = root.getDistrict().getDistrictID();

        if (id < rootID) {
            root.setLeft(deleteDistrict(root.getLeft(), id));
        } else if (id > rootID) {
            root.setRight(deleteDistrict(root.getRight(), id));
        } else {
            if (root.getLeft() == null) {
                return root.getRight();
            } else if (root.getRight() == null) {
                return root.getLeft();
            }

            root.setDistrict(findSmallestDistrict(root.getRight()));

            root.setRight(deleteDistrict(root.getRight(), root.getDistrict().getDistrictID()));
        }

        return root;
    }

    private District findSmallestDistrict(TreeNode node) {
        District smallest = node.getDistrict();
        while (node.getLeft() != null) {
            smallest = node.getLeft().getDistrict();
            node = node.getLeft();
        }
        return smallest;
    }

    /**
     * Eliminates a dueler from a pair of duelers.
     * - Both duelers in the DuelPair argument given will duel
     * - Winner gets returned to their District
     * - Eliminate a District if it only contains a odd person population or even
     * person population
     * 
     * @param pair of persons to fight each other.
     */

    public void eliminateDueler(DuelPair pair) {
        Person person1 = pair.getPerson1();
        Person person2 = pair.getPerson2();
        if (person1 != null && person2 != null) {
            Person winner = person1.duel(person2);

            int winnerDistrictID = winner.getDistrictID();
            District winnerDistrict = findDistrictByID(game, winnerDistrictID);
            if (winnerDistrict != null) {
                if (winner.getBirthMonth() % 2 == 1) {
                    winnerDistrict.addOddPerson(winner);
                } else {
                    winnerDistrict.addEvenPerson(winner);
                }
            }

            int loserDistrictID = (winner == person1) ? person2.getDistrictID() : person1.getDistrictID();
            District loserDistrict = findDistrictByID(game, loserDistrictID);

            if (loserDistrict != null) {
                int oddPopulationSize = loserDistrict.getOddPopulation().size();
                int evenPopulationSize = loserDistrict.getEvenPopulation().size();

                if (oddPopulationSize == 0 || evenPopulationSize == 0) {
                    eliminateDistrict(loserDistrictID);
                }
            }
        } else {
            if (person1 != null) {
                int person1DistrictID = person1.getDistrictID();
                District person1District = findDistrictByID(game, person1DistrictID);

                if (person1District != null) {
                    if (person1.getBirthMonth() % 2 == 1) {
                        person1District.addOddPerson(person1);
                    } else {
                        person1District.addEvenPerson(person1);
                    }
                }
            }

            if (person2 != null) {
                int person2DistrictID = person2.getDistrictID();
                District person2District = findDistrictByID(game, person2DistrictID);

                if (person2District != null) {
                    if (person2.getBirthMonth() % 2 == 1) {
                        person2District.addOddPerson(person2);
                    } else {
                        person2District.addEvenPerson(person2);
                    }
                }
            }
        }
    }

    private District findDistrictByID(TreeNode root, int id) {
        if (root == null) {
            return null;
        }

        int districtID = root.getDistrict().getDistrictID();

        if (id < districtID) {
            return findDistrictByID(root.getLeft(), id);
        } else if (id > districtID) {
            return findDistrictByID(root.getRight(), id);
        } else {
            return root.getDistrict();
        }
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * 
     * Obtains the list of districts for the Driver.
     * 
     * @return the ArrayList of districts for selection
     */
    public ArrayList<District> getDistricts() {
        return this.districts;
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * 
     * Returns the root of the BST
     */
    public TreeNode getRoot() {
        return game;
    }
}
