import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class VanillaFastPathsCheck {

	public static void main(String[] args) {
		Random random = new Random(7);
		for (int i = 0; i < 20000; i++) {
			checkRotation(random);
			checkRange(random);
		}
		System.out.println("VanillaFastPathsCheck ok");
	}

	private static void checkRotation(Random random) {
		int axis = random.nextInt(3);
		float angle = (random.nextFloat() * 2.0F - 1.0F) * (float) Math.PI * 2.0F;
		Quaternionf q = axis == 0 ? new Quaternionf().rotationX(angle) : axis == 1 ? new Quaternionf().rotationY(angle) : new Quaternionf().rotationZ(angle);
		Matrix4f pose = new Matrix4f().translation(random.nextFloat() * 8, random.nextFloat() * 8, random.nextFloat() * 8)
				.rotateXYZ(random.nextFloat(), random.nextFloat(), random.nextFloat()).scale(0.5F + random.nextFloat());
		Matrix3f normal = new Matrix3f(pose);
		Matrix4f expectedPose = new Matrix4f(pose).rotate(q);
		Matrix3f expectedNormal = new Matrix3f(normal).rotate(q);
		float v = axis == 0 ? q.x : axis == 1 ? q.y : q.z, w = q.w, s = 2.0F * v * w, c = w * w - v * v;
		rotate(pose, axis, s, c);
		rotate(normal, axis, s, c);
		for (int col = 0; col < 4; col++) {
			for (int row = 0; row < 4; row++) {
				assert Math.abs(pose.get(col, row) - expectedPose.get(col, row)) < 1.0E-4F : axis + " " + angle + " " + col + "," + row;
			}
		}
		for (int col = 0; col < 3; col++) {
			for (int row = 0; row < 3; row++) {
				assert Math.abs(normal.get(col, row) - expectedNormal.get(col, row)) < 1.0E-4F : axis + " " + angle + " " + col + "," + row;
			}
		}
		assert (pose.properties() & Matrix4f.PROPERTY_AFFINE) != 0;
	}

	private static void rotate(Matrix4f m, int axis, float s, float c) {
		switch (axis) {
			case 0 -> {
				float a0 = m.m10(), a1 = m.m11(), a2 = m.m12(), a3 = m.m13(), b0 = m.m20(), b1 = m.m21(), b2 = m.m22(), b3 = m.m23();
				m.m10(c * a0 + s * b0).m11(c * a1 + s * b1).m12(c * a2 + s * b2).m13(c * a3 + s * b3);
				m.m20(c * b0 - s * a0).m21(c * b1 - s * a1).m22(c * b2 - s * a2).m23(c * b3 - s * a3);
			}
			case 1 -> {
				float a0 = m.m20(), a1 = m.m21(), a2 = m.m22(), a3 = m.m23(), b0 = m.m00(), b1 = m.m01(), b2 = m.m02(), b3 = m.m03();
				m.m20(c * a0 + s * b0).m21(c * a1 + s * b1).m22(c * a2 + s * b2).m23(c * a3 + s * b3);
				m.m00(c * b0 - s * a0).m01(c * b1 - s * a1).m02(c * b2 - s * a2).m03(c * b3 - s * a3);
			}
			default -> {
				float a0 = m.m00(), a1 = m.m01(), a2 = m.m02(), a3 = m.m03(), b0 = m.m10(), b1 = m.m11(), b2 = m.m12(), b3 = m.m13();
				m.m00(c * a0 + s * b0).m01(c * a1 + s * b1).m02(c * a2 + s * b2).m03(c * a3 + s * b3);
				m.m10(c * b0 - s * a0).m11(c * b1 - s * a1).m12(c * b2 - s * a2).m13(c * b3 - s * a3);
			}
		}
	}

	private static void rotate(Matrix3f n, int axis, float s, float c) {
		switch (axis) {
			case 0 -> {
				float a0 = n.m10, a1 = n.m11, a2 = n.m12, b0 = n.m20, b1 = n.m21, b2 = n.m22;
				n.m10 = c * a0 + s * b0; n.m11 = c * a1 + s * b1; n.m12 = c * a2 + s * b2;
				n.m20 = c * b0 - s * a0; n.m21 = c * b1 - s * a1; n.m22 = c * b2 - s * a2;
			}
			case 1 -> {
				float a0 = n.m20, a1 = n.m21, a2 = n.m22, b0 = n.m00, b1 = n.m01, b2 = n.m02;
				n.m20 = c * a0 + s * b0; n.m21 = c * a1 + s * b1; n.m22 = c * a2 + s * b2;
				n.m00 = c * b0 - s * a0; n.m01 = c * b1 - s * a1; n.m02 = c * b2 - s * a2;
			}
			default -> {
				float a0 = n.m00, a1 = n.m01, a2 = n.m02, b0 = n.m10, b1 = n.m11, b2 = n.m12;
				n.m00 = c * a0 + s * b0; n.m01 = c * a1 + s * b1; n.m02 = c * a2 + s * b2;
				n.m10 = c * b0 - s * a0; n.m11 = c * b1 - s * a1; n.m12 = c * b2 - s * a2;
			}
		}
	}

	private static void checkRange(Random random) {
		int x1 = random.nextInt(9) - 4, y1 = random.nextInt(9) - 4, z1 = random.nextInt(9) - 4;
		int x2 = x1 + random.nextInt(5), y2 = y1 + random.nextInt(5), z2 = z1 + random.nextInt(5);
		List<long[]> expected = new ArrayList<>();
		int sx = x2 - x1 + 1, sy = y2 - y1 + 1, total = sx * sy * (z2 - z1 + 1);
		for (int index = 0; index < total; index++) {
			expected.add(new long[]{x1 + index % sx, y1 + index / sx % sy, z1 + index / sx / sy});
		}
		List<long[]> actual = new ArrayList<>();
		int x = x1, y = y1, z = z1;
		while (z <= z2) {
			actual.add(new long[]{x, y, z});
			if (++x > x2) {
				x = x1;
				if (++y > y2) {
					y = y1;
					++z;
				}
			}
		}
		assert actual.size() == expected.size();
		for (int i = 0; i < expected.size(); i++) {
			assert expected.get(i)[0] == actual.get(i)[0] && expected.get(i)[1] == actual.get(i)[1] && expected.get(i)[2] == actual.get(i)[2] : i;
		}
	}
}
