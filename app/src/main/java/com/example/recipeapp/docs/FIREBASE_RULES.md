# Firebase Security Rules Documentation

## Current Status: TEST MODE ⚠️

**IMPORTANT:** Production deployment requires rule updates!

## Current Rules (Development)
```json
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}
```

## Recommended Production Rules
```json
{
  "rules": {
    "mealPlans": {
      "$userId": {
        ".read": "$userId === auth.uid",
        ".write": "$userId === auth.uid",
        "$planId": {
          ".validate": "newData.hasChildren(['recipeId', 'recipeTitle', 'day', 'mealType', 'userId', 'timestamp']) && 
                       newData.child('recipeTitle').val().length > 0 &&
                       newData.child('recipeTitle').val().length <= 200"
        }
      }
    },
    "comments": {
      "$recipeId": {
        ".read": "auth != null",
        "$commentId": {
          ".write": "auth != null && (!data.exists() || data.child('userId').val() === auth.uid)",
          ".validate": "newData.hasChildren(['id', 'recipeId', 'userId', 'userName', 'userEmail', 'text', 'timestamp']) &&
                       newData.child('text').val().length > 0 &&
                       newData.child('text').val().length <= 500 &&
                       newData.child('userId').val() === auth.uid"
        }
      }
    },
    "favorites": {
      "$userId": {
        ".read": "$userId === auth.uid",
        ".write": "$userId === auth.uid"
      }
    }
  }
}
```

## Rule Explanations

### Meal Plans
- **Path**: `/mealPlans/{userId}/{planId}`
- **Read**: Users can ONLY read their own meal plans
- **Write**: Users can ONLY write to their own meal plans
- **Validation**:
    - All required fields must be present
    - Title must be 1-200 characters
    - Cannot write to other users' data

### Comments
- **Path**: `/comments/{recipeId}/{commentId}`
- **Read**: Any authenticated user can read all comments
- **Write**:
    - Can create new comments (if data doesn't exist)
    - Can only edit/delete own comments (userId must match)
- **Validation**:
    - Text must be 1-500 characters
    - userId must match authenticated user
    - All required fields present

### Favorites (Partner A)
- **Path**: `/favorites/{userId}`
- **Read/Write**: User can only access their own favorites

## Security Best Practices

1. **User ID Validation**: Always check `auth.uid` matches `$userId`
2. **Input Length Limits**: Prevent abuse with character limits
3. **Required Fields**: Use `.validate` to ensure data integrity
4. **No Global Write**: Never allow unauthenticated writes

## Testing Rules

Use Firebase Console → Realtime Database → Rules Playground:
```
Test 1: Read Own Meal Plans
Auth: user123
Path: /mealPlans/user123
Operation: read
Expected: ✅ Allow

Test 2: Read Other's Meal Plans
Auth: user123
Path: /mealPlans/user456
Operation: read
Expected: ❌ Deny

Test 3: Write Comment as Authenticated User
Auth: user123
Path: /comments/recipe1/comment1
Data: {userId: "user123", text: "Great!", ...}
Operation: write
Expected: ✅ Allow

Test 4: Edit Other's Comment
Auth: user123
Path: /comments/recipe1/comment2
Existing: {userId: "user456", ...}
Operation: write
Expected: ❌ Deny
```

## Deployment Checklist

Before production:
- [ ] Replace test rules with production rules
- [ ] Test all rules in Rules Playground
- [ ] Verify user-scoped paths work correctly
- [ ] Test edge cases (null values, missing fields)
- [ ] Monitor database usage in first week