using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class EnemyGenerator : MonoBehaviour
{
    public float generatorRate = 5;
    public GameObject asteroid;

    private IEnumerator Start()
    {
        while (true)
        {
            Instantiate(asteroid, transform.position, Quaternion.identity);
            yield return new WaitForSeconds(generatorRate);
        }
    }
}
