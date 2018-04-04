package tk.ubublik.huffmancoding.logic;

import java.util.Map;

public class RecursiveTreeUpdater implements TreeUpdater {

    @Override
    public void update(Leaf tree) {
        boolean stillUpdating;
        do {
            stillUpdating = false;
            int maxDepth = getMaxTreeDepth(tree);
            for (int i = maxDepth; i > 1; i--){
                Pair<Leaf, Leaf> maxChildren = getTheMostWeightFromDepth(tree, i, true);
                Pair<Leaf, Leaf> minParent = getTheMostWeightFromDepth(tree, i-1, false);
                if (maxChildren.first.getWeight()>minParent.first.getWeight()){
                    stillUpdating = true;
                    replaceChildren(minParent.second, minParent.first, maxChildren.first);
                    replaceChildren(maxChildren.second, maxChildren.first, minParent.first);
                    fixWeights(tree, minParent.first);
                    fixWeights(tree, maxChildren.first);
                    break;
                }
            }
        } while (stillUpdating);
    }

    private boolean fixWeights(Leaf tree, Leaf leaf){
        if (tree == leaf){
            if (leaf.getCharacter()==null) leaf.fixWeight();
            return true;
        }
        boolean result = false;
        if (tree.getLeft()!=null) result = fixWeights(tree.getLeft(), leaf);
        result = (result || (tree.getRight()!=null && fixWeights(tree.getRight(), leaf)));
        if (result && tree.getCharacter()==null) tree.fixWeight();
        return result;
    }

    private void replaceChildren(Leaf parentLeaf, Leaf currentLeaf, Leaf replaceLeaf){
        if (parentLeaf.getLeft()==currentLeaf){
            parentLeaf.setLeft(replaceLeaf);
        } else {
            parentLeaf.setRight(replaceLeaf);
        }
    }

    private int getMaxTreeDepth(Leaf leaf){
        int maxResult = 0;
        if (leaf.getLeft()!=null) maxResult = getMaxTreeDepth(leaf.getLeft());
        if (leaf.getRight()!=null) maxResult = Math.max(maxResult, getMaxTreeDepth(leaf.getRight()));
        return ++maxResult;
    }

    private Pair<Leaf, Leaf> getTheMostWeightFromDepth(Leaf leaf, int depth, boolean max){
        if (depth<1) throw new IllegalArgumentException("Depth cannot be less than 1");
        if (depth==1) return new Pair<>(leaf, null);
        Pair<Leaf, Leaf> left = leaf.getLeft()==null?null:getTheMostWeightFromDepth(leaf.getLeft(), depth-1, max);
        Pair<Leaf, Leaf> right = leaf.getRight()==null?null:getTheMostWeightFromDepth(leaf.getRight(), depth-1, max);
        Pair<Leaf, Leaf> most = getMinMax(left, right, max);
        if (most!=null && most.second==null) most.second = leaf;
        return most;
    }

    private Pair<Leaf, Leaf> getMinMax(Pair<Leaf, Leaf> left, Pair<Leaf, Leaf> right, boolean max){
        if (left==null) return right;
        if (right==null) return left;
        return (max == left.first.getWeight()>right.first.getWeight())?left:right;
    }
}
