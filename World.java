package core;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.*;

public class World {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 50;
    private Random random;
    private TETile[][] world;
    private Map<Integer, List<Integer>> starting;
    private Map<Integer, List<Integer>> topLeft;
    private Map<Integer, List<Integer>> bottom;
    private Map<Integer, List<Integer>> hws;
    private Map<Integer, Boolean> used;

    public World(long seed) {
        seed = seed;
        random = new Random(seed);
        world = new TETile[WIDTH][HEIGHT];
        if (HEIGHT > 0 && WIDTH > 0) {
            fillWithNothing(world);
        }
        starting = new TreeMap<>();
        topLeft = new TreeMap<>();
        bottom = new TreeMap<>();
        hws = new TreeMap<>();
        roomsGen();
    }

    public void fillWithNothing(TETile[][] tiles) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    //Picks a random number of rooms -- maybe from range 10-25, and puts them in the world
    //MOSTLY WORKING TRY***
    public void roomsGen() {
        int rooms = random.nextInt(12, 21);
        int count = 0;
        for (int i = 0; i < rooms; i++) {
            int nwidth = random.nextInt(5, 20);
            int nheight = random.nextInt(3, 10);
            int y = random.nextInt((i % 5) * 10, ((i % 5) * 10) + 10 - nheight);
            int x = random.nextInt((i % 4) * 20, ((i % 4) * 20) + 20 - nwidth);
            starting.put(count, new ArrayList<>());
            starting.get(count).add(x);
            starting.get(count).add(random.nextInt(y, y + nheight - 2));
            topLeft.put(count, new ArrayList<>());
            topLeft.get(count).add(random.nextInt(x, x + nwidth - 4));
            topLeft.get(count).add(y + nheight);
            bottom.put(count, new ArrayList<>());
            bottom.get(count).add(random.nextInt(x + 1, x + nwidth - 3));
            bottom.get(count).add(y);
            hws.put(count, new ArrayList<>());
            hws.get(count).add(nwidth);
            hws.get(count).add(nheight);
            count += 1;
            for (int k = 0; k <= nwidth; k++) {
                for (int j = 0; j <= nheight; j++) {
                    if (k == 0 || j == nheight || k == nwidth || j == 0) {
                        world[x + k][j + y] = Tileset.WALL;
                    } else {
                        world[k + x][j + y] = Tileset.FLOOR;
                    }
                }
            }
        }
        createHalls();
        finalCheck();
    }
    public void createHalls() {
        starting();
        topLeft();
        bottom();
        for (int i : starting.keySet()) {
            List<Integer> room = new ArrayList<>();
            room.add(starting.get(i).get(0) + hws.get(i).get(0));
            room.add(bottom.get(i).get(1));
            rightHalls(room);
        }
        starting();
        topLeft();
        bottom();
    }
    public void starting() {
        for (int i : starting.keySet()) {
            leftHalls(starting.get(i));
        }
    }
    public void topLeft() {
        for (int j : topLeft.keySet()) {
            topHalls(topLeft.get(j));
        }
    }
    public void bottom() {
        for (int i = 0; i < bottom.size(); i++) {
            bottomHalls(bottom.get(i));
        }
    }

    public void leftHalls(List room) {
        int x1 = (int) room.get(0);
        int y1 = (int) room.get(1);
        if (leftHas(x1, y1)) {
            world[x1][y1 + 1] = Tileset.FLOOR;
            for (int i = x1 - 1; i > 0; i--) {
                boolean temp = world[i][y1] == Tileset.NOTHING && world[i][y1 + 2] == Tileset.NOTHING;
                boolean temp2 = temp && world[i][y1 + 1] == Tileset.NOTHING;
                if (world[i][y1] == Tileset.WALL && world[i][y1 + 2] == Tileset.WALL) {
                    world[i][y1 + 1] = Tileset.FLOOR;
                    break;
                } else if (temp2) {
                    world[i][y1] = Tileset.WALL;
                    world[i][y1 + 2] = Tileset.WALL;
                    world[i][y1 + 1] = Tileset.FLOOR;
                }
            }
        }
    }

    public boolean leftHas(int x1, int y1) {
        for (int i = x1 - 1; i >= 0; i--) {
            if (i > 0) {
                boolean temp1 = world[i][y1] == Tileset.WALL;
                if (temp1 && world[i][y1 + 2] == Tileset.WALL && world[i][y1 + 1] == Tileset.WALL) {
                    return true;
                } else if (temp1 || world[i][y1 + 2] == Tileset.WALL || world[i][y1 + 1] == Tileset.WALL) {
                    return false;
                }
            }
        }
        return false;
    }
    public void topHalls(List room) {
        int x1 = (int) room.get(0);
        int y1 = (int) room.get(1);
        if (topHas(x1, y1)) {
            world[x1 + 1][y1] = Tileset.FLOOR;
            for (int j = y1 + 1; j < HEIGHT; j++) {
                boolean temp = world[x1][j] == Tileset.NOTHING && world[x1 + 1][j] == Tileset.NOTHING;
                boolean temp2 = temp && world[x1 + 2][j] == Tileset.NOTHING;
                if (world[x1][j] == Tileset.WALL && world[x1 + 2][j] == Tileset.WALL) {
                    world[x1 + 1][j] = Tileset.FLOOR;
                    break;
                } else if (temp2) {
                    world[x1][j] = Tileset.WALL;
                    world[x1 + 2][j] = Tileset.WALL;
                    world[x1 + 1][j] = Tileset.FLOOR;
                }
            }
        }
    }
    public boolean topHas(int x1, int y1) {
        for (int j = y1 + 1; j < HEIGHT; j++) {
            if (j < HEIGHT - 1) {
                boolean temp1 = world[x1][j] == Tileset.WALL;
                if (temp1 && world[x1 + 2][j] == Tileset.WALL && world[x1 + 1][j] == Tileset.WALL) {
                    return true;
                } else if (temp1 || world[x1 + 2][j] == Tileset.WALL || world[x1 + 1][j] == Tileset.WALL) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }
    public void bottomHalls(List room) {
        int x1 = (int) room.get(0);
        int y1 = (int) room.get(1);
        if (bottomHas(x1, y1)) {
            world[x1 + 1][y1] = Tileset.FLOOR;
            for (int j = y1 - 1; j > 0; j--) {
                boolean temp = world[x1][j] == Tileset.NOTHING && world[x1 + 1][j] == Tileset.NOTHING;
                boolean temp2 = temp && world[x1 + 2][j] == Tileset.NOTHING;
                if (world[x1][j] == Tileset.WALL && world[x1 + 2][j] == Tileset.WALL) {
                    world[x1 + 1][j] = Tileset.FLOOR;
                    break;
                } else if (temp2) {
                    world[x1][j] = Tileset.WALL;
                    world[x1 + 2][j] = Tileset.WALL;
                    world[x1 + 1][j] = Tileset.FLOOR;
                }
            }
        }
    }

    public void rightHalls(List room) {
        int x1 = (int) room.get(0);
        int y1 = (int) room.get(1);
        if (rightHas(x1, y1)) {
            world[x1][y1 + 1] = Tileset.FLOOR;
            for (int i = x1 + 1; i < WIDTH; i++) {
                boolean temp = world[i][y1] == Tileset.NOTHING && world[i][y1 + 2] == Tileset.NOTHING;
                boolean temp2 = temp && world[i][y1 + 1] == Tileset.NOTHING;
                if (world[i][y1] == Tileset.WALL && world[i][y1 + 2] == Tileset.WALL) {
                    world[i][y1 + 1] = Tileset.FLOOR;
                    break;
                } else if (temp2) {
                    world[i][y1] = Tileset.WALL;
                    world[i][y1 + 2] = Tileset.WALL;
                    world[i][y1 + 1] = Tileset.FLOOR;
                }
            }
        }
    }

    public boolean rightHas(int x1, int y1) {
        for (int i = x1 + 1; i < WIDTH; i++) {
            if (i < WIDTH) {
                boolean temp1 = world[i][y1] == Tileset.WALL;
                if (temp1 && world[i][y1 + 2] == Tileset.WALL && world[i][y1 + 1] == Tileset.WALL) {
                    return true;
                } else if (temp1 || world[i][y1 + 2] == Tileset.WALL || world[i][y1 + 1] == Tileset.WALL) {
                    return false;
                }
            }
        }
        return false;
    }

    public boolean bottomHas(int x1, int y1) {
        for (int j = y1 - 1; j >= 0; j--) {
            if (j >= 0) {
                if (j == 0) {
                    return false;
                }
                boolean temp1 = world[x1][j] == Tileset.WALL;
                if (temp1 && world[x1 + 2][j] == Tileset.WALL && world[x1 + 1][j] == Tileset.WALL) {
                    return true;
                } else if (temp1 || world[x1 + 2][j] == Tileset.WALL || world[x1 + 1][j] == Tileset.WALL) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    //CHECK all rooms and if room is surrounded by nothing or boundaries, then set it to nothing
    public void finalCheck() {
        for (int i : starting.keySet()) {
            int x1 = starting.get(i).get(0);
            int width = hws.get(i).get(0);
            int height = hws.get(i).get(1);
            int y1 = bottom.get(i).get(1);
            if (checkAround(x1, y1, width, height)) {
                for (int k = 0; k <= width; k++) {
                    for (int j = 0; j <= height; j++) {
                        world[x1 + k][j + y1] = Tileset.NOTHING;
                    }
                }
            }
        }
    }
    public boolean checkAround(int x1, int y1, int width, int height) {
        if (y1 - 1 >= 0 && x1 >= 0) {
            for (int i = x1; i <= x1 + width; i++) {
                if (world[i][y1 - 1] != Tileset.NOTHING) {
                    return false;
                }
            }
        }
        if (y1 + height < HEIGHT - 1 && y1 >= 0 && x1 >= 0) {
            for (int i = x1; i <= x1 + width; i++) {
                if (world[i][y1 + height + 1] != Tileset.NOTHING) {
                    return false;
                }
            }
        }
        if (x1 - 1 >= 0 && y1 >= 0) {
            for (int j = y1; j <= y1 + height; j++) {
                if (world[x1 - 1][j] != Tileset.NOTHING) {
                    return false;
                }
            }
        }
        if (x1 + width < WIDTH - 1 && y1 >= 0 && x1 >= 0) {
            for (int j = y1; j <= y1 + height; j++) {
                if (world[x1 + width + 1][j] != Tileset.NOTHING) {
                    return false;
                }
            }
        }
        return true;
    }

    public TETile[][] currState() {
        return world;
    }

}
