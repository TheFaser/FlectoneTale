package net.flectone.tale.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DistanceUtil {

    public Optional<Double> distance(@Nonnull PlayerRef playerOne, @Nonnull PlayerRef playerTwo) {
        UUID worldOne = playerOne.getWorldUuid();
        UUID worldTwo = playerTwo.getWorldUuid();
        if (!Objects.equals(worldOne, worldTwo)) return Optional.empty();

        Vector3d positionOne = playerOne.getTransform().getPosition();
        Vector3d positionTwo = playerTwo.getTransform().getPosition();
        double distance = Math.sqrt(square(positionOne.x - positionTwo.x) + square(positionOne.y - positionTwo.y) + square(positionOne.z - positionTwo.z));
        return Optional.of(distance);
    }

    private double square(double num) {
        return num * num;
    }

}
