package com.eon37_dev.tmh.services;

import com.eon37_dev.tmh.model.Post;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class PostService {
  public static class Author {
    private static final ArrayList<String> USER_LIST = new ArrayList<>(Arrays.asList("Ant", "Aphid", "Armyworm", "Bagworm", "Bark beetle", "Bee", "Beetle", "Biting midge", "Black fly", "Black widow spider", "Blister beetle", "Booklice", "Botfly", "Boxelder bug", "Burying beetle", "Buttercup moth", "Butterfly", "Caddisfly", "Cane beetle", "Carpet beetle", "Cecropia moth", "Centipede", "Chalcid wasp", "Chafer beetle", "Cicada", "Cockroach", "Crane fly", "Crickets", "Damselfly", "Damsel bug", "Deathwatch beetle", "Deer fly", "Digger wasp", "Dobsonfly", "Dragonfly", "Drone fly", "Drywood termite", "Earwig", "Elm leaf beetle", "Firefly", "Flea", "Flour beetle", "Fly", "Fungus gnat", "Gall wasp", "Giant water bug", "Glow worm", "Gnats", "Grasshopper", "Green stink bug", "Gypsy moth", "Harlequin bug", "Harvester ant", "Hawk moth", "Head louse", "Horntail", "Horsefly", "Hoverfly", "Housefly", "Ichneumon wasp", "June beetle", "Lace bug", "Lacewing", "Leafminer", "Leaf-footed bug", "Mediterranean fruit fly", "Midge", "Mud dauber wasp", "Net-winged insect", "Nettle caterpillar", "Nymph", "Orchid bee", "Pellucid moth", "Pollinator", "Pyralid moth", "Red-legged grasshopper", "Slug caterpillar moth", "Zigzag leafhopper", "Zopherid beetle", "Zygaenid moth", "Ambush bug", "Anobiid beetle", "Antlion", "Aphid midge", "Assassin bug", "Bagworm moth", "Bald-faced hornet", "Bark lice", "Bark weevil", "Bat bug", "Buprestid beetle", "Carpenter bee", "Carpenter ant", "Carpet moth", "Carrion beetle", "Casebearer moth", "Cat flea", "Chigger", "Chrysomelid beetle", "Cicadellid", "Citrus leafminer", "Click beetle", "Clown beetle", "Colorado potato beetle", "Corn earworm", "Cotton bollworm", "Cranefly", "Cucumber beetle", "Cuckoo wasp", "Darkling beetle", "Desert locust", "Dock beetle", "Dog flea", "Dung beetle", "Emerald ash borer", "European corn borer", "European hornet", "False blister beetle", "Fall armyworm", "Featherwing beetle", "Flea beetle", "Flower fly", "Flour moth", "Forest tent caterpillar", "Fungus beetle", "Gall midge", "Garden chafer", "Garden flea beetle", "German cockroach", "Giant lacewing", "Giant sphinx moth", "Golden tortoise beetle", "Green June beetle", "Green leafhopper", "Green lacewing", "Ground beetle", "Hairy caterpillar", "Harlequin ladybird", "Hedgehopper", "Horse chestnut leaf miner", "House cricket", "Hover fly", "Indian meal moth", "Japanese beetle", "Jewel beetle", "June bug", "Katydid", "Lady beetle", "Ladybug", "Leaf beetle", "Leafcutter ant", "Leafhopper", "Leafroller", "Leopard moth", "Lice", "Longhorn beetle", "Long-legged fly", "Luna moth", "Mantisfly", "May beetle", "Mayfly", "Mealworm beetle", "Metallic wood-boring beetle", "Mexican bean beetle", "Milkweed bug", "Miner bee", "Mole cricket", "Monarch butterfly", "Mosquito", "Mud dauber", "Mummy wasp", "Net-winged beetle", "New Zealand grass grub", "Northern corn rootworm", "Oak moth", "Oak leafroller", "Oakworm moth", "Oil beetle", "Orchard bee", "Oriental cockroach", "Paper wasp", "Parasitoid wasp", "Pea aphid", "Pepper weevil", "Pine beetle", "Pine processionary moth", "Plant bug", "Plume moth", "Potato beetle", "Predatory stink bug", "Praying mantis", "Psyllid", "Queen butterfly", "Red admiral butterfly", "Red imported fire ant", "Red flour beetle", "Rice weevil", "Robber fly", "Root maggot", "Sawfly", "Scorpionfly", "Seed bug", "Silk moth", "Silverfish", "Skipper butterfly", "Soldier beetle", "Spittlebug", "Squash bug", "Stag beetle", "Stink bug", "Swallowtail butterfly", "Sweat bee", "Syrphid fly", "Tabanid fly", "Tachinid fly", "Termite", "Thrips", "Tiger beetle", "Tiger moth", "Tineid moth", "Tipulid fly", "Tobacco hornworm", "Tortricid moth", "Treehopper", "Tse-tse fly", "True bug", "True weevil", "Underwing moth", "Velvet ant", "Vine weevil", "Water strider", "Water scavenger beetle", "Weevil", "Whitefly", "Woolly aphid", "Yellowjacket", "Zebra butterfly"));

    private static synchronized String getAuthor() {
      while (USER_LIST.size() == 0) {
        continue;
      }

      synchronized ("USER_LIST") {
        int i = ThreadLocalRandom.current().nextInt(USER_LIST.size());
        return USER_LIST.remove(i);
      }
    }

    private static void returnAuthor(String author) {
      synchronized ("USER_LIST") {
        USER_LIST.add(author);
      }
    }
  }
  private static final Map<String, String> ASS_USER_NAMES = new ConcurrentHashMap<>(Author.USER_LIST.size());
  public static void removeAuthor(String clientId) { //TODO refactor
    Author.returnAuthor(ASS_USER_NAMES.remove(clientId));
  }

  private static final Map<Long, Post> POSTS = new ConcurrentHashMap<>();
  public Map<Long, Post> getPosts() {
    return POSTS;
  }

  public Post newPost(String clientId, String text, boolean anonymous) {
    String author = ASS_USER_NAMES.computeIfAbsent(clientId, k -> Author.getAuthor());
    long createTime = System.nanoTime();

    Post newPost = Post.builder()
            .id(createTime)
            .clientId(clientId)
            .author(author)
            .anonymous(anonymous)
            .text(text)
            .build(true);

    POSTS.put(createTime, newPost);

    return newPost;
  }

  public int likePost(String clientId, Long id) {
    Optional<Post> post = Optional.ofNullable(POSTS.get(id));

    return post.map(op -> op.incrementLikes(clientId)).orElse(1);
  }

  public int likeComment(String clientId, Long id, Long commentId) {
    Optional<Post> post = Optional.ofNullable(POSTS.get(id));

    post.ifPresent(op -> op.getComments().get(commentId).incrementLikes(clientId));

    return post.map(op -> op.getComments().get(commentId).getLikes().size()).orElse(1);
  }

  public Map<Long, Post> newComment(String clientId, Long id, String text) {
    String author = ASS_USER_NAMES.computeIfAbsent(clientId, k -> Author.getAuthor());

    Optional<Post> post = Optional.ofNullable(POSTS.get(id));

    Long createTime = System.nanoTime();
    Post newComment = Post.builder()
            .id(createTime)
            .clientId(clientId)
            .author(author)
            .text(text)
            .build(false);

    post.ifPresent(op -> {
      op.getComments().put(createTime, newComment);

      op.incrementExpire();
    });

    return Map.of(createTime, newComment);
  }

  public void deletePostsByTime() {
    Set<Map.Entry<Long, Post>> toRemove = POSTS.entrySet().stream()
            .filter(entry -> entry.getValue().getExpire() <= System.nanoTime())
            .collect(Collectors.toSet());

    toRemove.forEach(entry -> POSTS.remove(entry.getKey()));
  }

  @Scheduled(fixedRate = 60000L)
  public void clearOld() {
    deletePostsByTime();
  }
}
